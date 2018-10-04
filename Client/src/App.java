public class App {
    public static void main(String[] args) {
        int id = Math.abs((int) (Math.random() * (1 << 30)));
        new ClientFrame(id);
    }
}
