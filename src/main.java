import java.io.*;
import java.net.*;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Host for client or '-s' for server and port excepted!");
            System.exit(0);
        }
        if (args[0].equals("-s")) server(Integer.parseInt(args[1]));
        else client(args[0], Integer.parseInt(args[1]));
    }
    public static void client(String host, int port) {
        Scanner in = new Scanner(System.in);
        int num;
        BufferedReader reader;
        BufferedWriter writer;
        try(Socket socket = new Socket(host, port)) {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (true) {
                System.out.println("Enter number:");
                String input = in.nextLine();
                input = "(?>" + input + ")";
                if (input.equals("")) {
                    System.out.println("Program finishing...");
                    break;
                }
                try {
                    num = Integer.parseInt(input);
                } catch (ClassCastException ex) {
                    System.out.println("Number expected");
                    continue;
                }
                writer.write(num);
                writer.flush();
                int fibonacci = reader.read();
                System.out.println("Fibonacci number " + num + " is " + fibonacci);
            }
            socket.close();
            reader.close();
            writer.close();
        }catch (SocketException e) {
            System.out.println("No connection");
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            System.out.println("Program failed because of: " + ex.getMessage());
        }
        in.close();
    }

    public static void server(int port) {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is ready");
            while(true) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected");
                new Thread(() -> {
                    BufferedWriter writer;
                    BufferedReader reader;
                    try {
                        writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        try {
                            while (client.isConnected()) {
                                    int num = reader.read();
                                    int fibonacci = getFibonacci(num);
                                    writer.write(fibonacci);
                                    writer.flush();
                            }
                        } finally {
                            Thread.currentThread().interrupt();
                            client.close();
                            reader.close();
                            writer.close();
                        }
                    } catch (IOException e) {
                        System.out.println("Client disconnected");
                    }
                }).start();
            }
        } catch (Exception e) {
            System.out.println("Server exception: ");
            e.printStackTrace();
        }
    }

    public static int getFibonacci(int index) {
        if (index <= 0) return 0;
        int last_1 = 0, last_0 = 1, current = 1;
            for (int i = 2; i <= index; i++) {
                current = last_1 + last_0;
                last_1 = last_0;
                last_0 = current;
            }
        return current;
    }
}


