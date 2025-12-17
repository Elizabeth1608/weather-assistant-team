public class NotificationClient {
    private String serverUrl = "URL не настроен";

    public void connect() {
        if (serverUrl.equals("URL не настроен")) {
            System.out.println("WebSocket: Ожидание настройки URL сервера");
        } else {
            System.out.println("WebSocket: Подключение к " + serverUrl);
        }
    }

    public void sendMessage(String message) {
        System.out.println("WebSocket: Сообщение подготовлено: " + message);
    }

    public void setServerUrl(String url) {
        this.serverUrl = url;
        System.out.println("WebSocket: URL установлен: " + url);
    }
}