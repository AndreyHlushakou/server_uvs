package by.agat.server_uvs.tcpserver;

import java.net.SocketAddress;
import java.util.Objects;

public record AddressMessage(SocketAddress clientAddress, byte[] message) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressMessage that = (AddressMessage) o;
        return Objects.equals(clientAddress, that.clientAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientAddress);
    }

    @Override
    public String toString() {
        return "MessageForClient{" + "clientAddress=" + clientAddress + '}';
    }
}
