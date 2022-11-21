package employee;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Employee implements Runnable {

    private final int employeeId;
    private final InetAddress group;
    private final int port;

    public Employee(int employeeId, InetAddress group, int port) {
        this.employeeId = employeeId;
        this.group = group;
        this.port = port;
    }

    @Override
    public void run() {
        try(MulticastSocket socket = new MulticastSocket(port)) {
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(1, 3));
            joinGroup(socket, group);
            sendMulticastMessage(socket, group, String.format("Hello, I'm employee %d", employeeId));
            receiveMulticastMessages(socket, group);
        } catch (IOException e) {
            System.out.printf("Employee %d: Input/Output error\n", employeeId);
        } catch (InterruptedException ignored) { }
    }

    private void sendMulticastMessage(MulticastSocket socket, InetAddress group, String message) throws IOException {
        byte[] messageBytes = message.getBytes();
        DatagramPacket outputDatagramPacket =
                new DatagramPacket(messageBytes, messageBytes.length, group, port);
        socket.send(outputDatagramPacket);
        System.out.printf("Employee %d - Sent message: \"%s\"\n", employeeId, message);
    }

    private void receiveMulticastMessages(MulticastSocket socket, InetAddress group) throws IOException {
        byte[] buffer = new byte[1000];
        String receivedMesssage;
        while (!Thread.currentThread().isInterrupted()) {
            DatagramPacket inputDatagramPacket =
                    new DatagramPacket(buffer, buffer.length);
            socket.receive(inputDatagramPacket);
            receivedMesssage = new String(inputDatagramPacket.getData(), 0, inputDatagramPacket.getLength());
            System.out.printf("Employee %d - Message received: %s\n", employeeId, receivedMesssage);
            if (receivedMesssage.equals("Go home")) break;
        }
        sendMulticastMessage(socket, group, "Bye bye");
        socket.leaveGroup(group);
        System.out.printf("Employee %d - Left group\n", employeeId);
    }

    private void joinGroup(MulticastSocket socket, InetAddress group) throws IOException {
        socket.joinGroup(group);
        System.out.printf("Employee %d - Joined group\n", employeeId);
    }

}
