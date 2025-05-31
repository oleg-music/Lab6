package git.olegmusic.server;

import git.olegmusic.common.CommandRequest;
import git.olegmusic.common.CommandResponse;
import git.olegmusic.common.Person;
import git.olegmusic.server.commandprocessing.core.Invoker;
import git.olegmusic.server.commandprocessing.utils.CollectionManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ServerMain {
    private static final int PORT = 4000;

    public static void main(String[] args) {
        try {
            Invoker invoker = new Invoker();
            invoker.loadCollection();
            Set<Person> persons = CollectionManager.getPersonSet();
            Integer maxId = 0;
            for (Person p : persons) {
                if (p.getId() > maxId) {
                    maxId = p.getId();
                }
            }
            Person.setIdCounter(maxId + 1);
            Runtime.getRuntime().addShutdownHook(new Thread(invoker::saveCollection));

            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress("0.0.0.0", PORT));
            serverChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server listening on port " + PORT);

            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String line = scanner.nextLine();
                    if (line.trim().equalsIgnoreCase("save")) {
                        try {
                            invoker.saveCollection();

                            System.out.println("Коллекция сохранена по команде с сервера.");
                        } catch (Exception e) {
                            System.err.println("Ошибка при сохранении: " + e.getMessage());
                        }
                    } else if (line.trim().equalsIgnoreCase("exit")) {
                        System.out.println("Сервер завершает работу...");
                        System.exit(0);
                    } else {
                        System.out.println("Неизвестная серверная команда: " + line);
                    }
                }
            }).start();


            Map<SocketChannel, ClientContext> contexts = new HashMap<>();

            while (true) {
                selector.select();
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if (key.isAcceptable()) {
                        ServerSocketChannel srv = (ServerSocketChannel) key.channel();
                        SocketChannel client = srv.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                        contexts.put(client, new ClientContext());
                        System.out.println("Client connected: " + client.getRemoteAddress());

                    } else if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ClientContext ctx = contexts.get(client);
                        try {
                            if (!handleRead(client, ctx, invoker, key)) {
                                contexts.remove(client);
                                key.cancel();
                                client.close();
                            }
                        } catch (IOException e) {
                            System.err.println("Connection with client lost: " + e.getMessage());
                            contexts.remove(client);
                            key.cancel();
                            client.close();
                        }

                    } else if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ClientContext ctx = contexts.get(client);
                        handleWrite(client, ctx, key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean handleRead(SocketChannel client,
                                      ClientContext ctx,
                                      git.olegmusic.server.commandprocessing.core.Invoker invoker,
                                      SelectionKey key) throws IOException {
        if (ctx.lenBuffer.hasRemaining()) {
            int r = client.read(ctx.lenBuffer);
            if (r == -1) return false;
            if (ctx.lenBuffer.hasRemaining()) return true;
            ctx.lenBuffer.flip();
            int length = ctx.lenBuffer.getInt();
            ctx.msgBuffer = ByteBuffer.allocate(length);
        }
        int r = client.read(ctx.msgBuffer);
        if (r == -1) return false;
        if (ctx.msgBuffer.hasRemaining()) return true;

        ctx.msgBuffer.flip();
        CommandRequest request = deserialize(ctx.msgBuffer);
        CommandResponse response = invoker.process(request);
        ByteBuffer outBuf = serialize(response);
        ctx.responses.add(outBuf);
        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);

        ctx.lenBuffer.clear();
        ctx.msgBuffer = null;
        return true;
    }

    private static void handleWrite(SocketChannel client,
                                    ClientContext ctx,
                                    SelectionKey key) throws IOException {
        Queue<ByteBuffer> queue = ctx.responses;
        while (!queue.isEmpty()) {
            ByteBuffer buf = queue.peek();
            client.write(buf);
            if (buf.hasRemaining()) break;
            queue.poll();
        }
        if (queue.isEmpty()) {
            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
        }
    }

    private static CommandRequest deserialize(ByteBuffer buf) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(buf.array());
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (CommandRequest) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    private static ByteBuffer serialize(CommandResponse resp) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(resp);
        }
        byte[] data = bos.toByteArray();
        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES + data.length);
        buf.putInt(data.length).put(data).flip();
        return buf;
    }

    private static class ClientContext {
        ByteBuffer lenBuffer = ByteBuffer.allocate(Integer.BYTES);
        ByteBuffer msgBuffer;
        Queue<ByteBuffer> responses = new LinkedList<>();
    }
}