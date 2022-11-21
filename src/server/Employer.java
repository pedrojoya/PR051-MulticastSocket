package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Employer implements Runnable {

    private final int port;
    private final InetAddress group;

    public Employer(InetAddress group, int port) {
        this.group = group;
        this.port = port;
    }

    @Override
    public void run() {
        try(MulticastSocket socket = new MulticastSocket(port)) {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(1, 3));
            sendMulticastMessage(socket, group, "Hello, employees. I'm the employer");
            Thread chat = new Thread(new EmployeesChat(group, port));
            chat.start();
            TimeUnit.SECONDS.sleep(10);
            sendMulticastMessage(socket, group, "Go home");
        } catch (IOException e) {
            System.out.println("Employer - Input/Output error");
        } catch (InterruptedException ignored) { }
    }

    private void sendMulticastMessage(MulticastSocket socket, InetAddress group, String message) throws IOException {
        byte[] messageBytes = message.getBytes();
        DatagramPacket outputDatagramPacket =
                new DatagramPacket(messageBytes, messageBytes.length, group, port);
        socket.send(outputDatagramPacket);
        System.out.printf("Employer - Sent message: \"%s\"\n", message);
    }

}
