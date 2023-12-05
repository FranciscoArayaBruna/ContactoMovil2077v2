package com.pancho.contactomovil2077.Controlador;

public interface MqttConnectionListener {
    void onSuccess();

    void onFailure(Throwable exception);
}
