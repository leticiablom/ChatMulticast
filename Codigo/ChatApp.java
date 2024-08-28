import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

public class ChatApp {
    private String user;
    private String host;
    private static final int PORT = 6789;
    MulticastSocket mSocket = null;
    InetAddress groupIp = null;
    InetSocketAddress group = null;

    private boolean enterRoom() {
        try {
            groupIp = InetAddress.getByName(host);
            group = new InetSocketAddress(groupIp, PORT);

            mSocket = new MulticastSocket(PORT);
            mSocket.joinGroup(group, null);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    
  private boolean sendMessage(String message) {
    try {
      byte[] buffer = message.getBytes();
      DatagramPacket messageOut = new DatagramPacket(buffer, buffer.length, groupIp, PORT);
      mSocket.send(messageOut);
    } catch (IOException e) {
      System.out.println("Erro ao enviar mensagem: " + e.getMessage());
    }
    return true;
  }

    private void leaveRoom() {
        try {
            mSocket.leaveGroup(group, null);
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            System.out.println(
                    "Ocorreu um erro ao sair do grupo. Verifique se o endereço está correto e tente novamente.");
        }
    }

    private boolean isConnected() {
        return mSocket != null && !mSocket.isClosed();
    }
    
    private void receiveMessages() {
        byte[] buffer = new byte[1000];
        try {
            while (isConnected()) {
                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                mSocket.receive(messageIn);
                System.out.println("Recebido: " + new String(messageIn.getData()).trim());
                buffer = new byte[1000];
            }
        } catch (IOException e) {
            if (isConnected()) {
                System.out.println("Erro ao receber mensagem: " + e.getMessage());
            }
        }
    }


    

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("É preciso executar o programa passando o nome do usuário e o host");
            return;
        }
    
        ChatApp chatApp = new ChatApp();
    
        chatApp.user = args[0];
        chatApp.host = args[1];
    
        if (!chatApp.enterRoom()) {
            System.out.println("Não foi possível entrar no grupo multicast");
            return;
        }
    
        chatApp.sendMessage("Usuario " + chatApp.user + " entrou na sala");
    
        new Thread(chatApp::receiveMessages).start();
    
        new Thread(() -> {
            try (java.util.Scanner scanner = new java.util.Scanner(System.in)) {
                while (chatApp.isConnected()) {
                    String message = scanner.nextLine();
                    if (message.equalsIgnoreCase("sair")) {
                        chatApp.sendMessage("Usuário " + chatApp.user + " saiu da sala");
                        chatApp.leaveRoom();
                        break;
                    }
                    chatApp.sendMessage(chatApp.user + ": " + message);
                }
            }
        }).start();
    }
    
}