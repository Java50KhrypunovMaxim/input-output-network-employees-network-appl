package telran.net;

import java.io.Serializable;

public interface NetworkHandler {
<T> T send(String requestType, Serializable requestData);
}
