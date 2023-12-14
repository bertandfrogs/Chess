import ui.ConsoleOutput;

public class Main {
    public static void main(String[] args) {
        try {
            String ip = (args.length == 1) ? args[0] : null;
            Client client = new Client(ip);
            client.run();
        } catch (Exception e) {
            if(e.getMessage().contains("\"Client.consoleInput\" is null")){
                System.out.println(ConsoleOutput.getError("The client can't be run from inside IntelliJ, open an external terminal."));
            }
            else if(e.getMessage().contains("The connection has been closed.")){
                System.out.println(ConsoleOutput.getError("The connection has been closed."));
            }
            else {
                System.out.println(ConsoleOutput.getError("Error: " + e.getMessage()));
            }
        }
    }
}
