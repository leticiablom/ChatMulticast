import java.util.*;

public class Main {
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
