package com.pancho.contactomovil2077.Vista;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pancho.contactomovil2077.R;

import java.util.List;

public class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.MensajeViewHolder> {

    private List<String> listaMensajes;
    private List<String> mensajes;

    public MensajesAdapter(List<String> listaMensajes) {
        this.listaMensajes = listaMensajes;
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, parent, false);
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        String mensaje = listaMensajes.get(position);
        holder.bind(mensaje);
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    public void actualizarMensajes(List<String> nuevosMensajes) {
        mensajes.clear();
        mensajes.addAll(nuevosMensajes);
        notifyDataSetChanged();

    }

    public static class MensajeViewHolder extends RecyclerView.ViewHolder {

        private TextView txtMensaje;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMensaje = itemView.findViewById(R.id.txtMensaje);
        }

        public void bind(String mensaje) {
            txtMensaje.setText(mensaje);
        }
    }
}

