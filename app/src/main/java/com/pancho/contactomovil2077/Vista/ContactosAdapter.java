package com.pancho.contactomovil2077.Vista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pancho.contactomovil2077.Modelo.Contacto;
import com.pancho.contactomovil2077.R;

import java.util.List;

public class ContactosAdapter extends RecyclerView.Adapter<ContactosAdapter.ContactoViewHolder> {

    private List<Contacto> listaDeContactos;
    private OnContactoClickListener onContactoClickListener;
    private int posicionSeleccionada = RecyclerView.NO_POSITION;

    public ContactosAdapter(List<Contacto> listaDeContactos, OnContactoClickListener onContactoClickListener) {
        this.listaDeContactos = listaDeContactos;
        this.onContactoClickListener = onContactoClickListener;
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacto, parent, false);
        return new ContactoViewHolder(view, onContactoClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        Contacto contacto = listaDeContactos.get(position);
        holder.bind(contacto, position);
    }

    @Override
    public int getItemCount() {
        return listaDeContactos.size();
    }

    public void actualizarLista(List<Contacto> nuevosContactos) {
        listaDeContactos = nuevosContactos;
        notifyDataSetChanged();
    }

    public void deseleccionarItem() {
        if (posicionSeleccionada != RecyclerView.NO_POSITION) {
            posicionSeleccionada = RecyclerView.NO_POSITION;
            notifyDataSetChanged();
        }
    }

    public interface OnContactoClickListener {
        void onContactoClick(Contacto contacto);
    }
    public Contacto getItemSeleccionado() {
        if (posicionSeleccionada != RecyclerView.NO_POSITION) {
            return listaDeContactos.get(posicionSeleccionada);
        }
        return null;
    }

    // Método para obtener la lista de contactos
    public List<Contacto> getListaDeContactos() {
        return listaDeContactos;
    }

    // Método para actualizar la posición seleccionada
    public void actualizarPosicionSeleccionada(int posicion) {
        posicionSeleccionada = posicion;
        notifyDataSetChanged();
    }


    class ContactoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textNombre;
        private TextView textCorreo;
        private OnContactoClickListener onContactoClickListener;

        public ContactoViewHolder(@NonNull View itemView, OnContactoClickListener onContactoClickListener) {
            super(itemView);
            this.textNombre = itemView.findViewById(R.id.textNombre);
            this.textCorreo = itemView.findViewById(R.id.textCorreo);
            this.onContactoClickListener = onContactoClickListener;

            itemView.setOnClickListener(this);
        }

        public void bind(Contacto contacto, int position) {
            if (contacto!=null){
                textNombre.setText(contacto.getNombre());
                textCorreo.setText(contacto.getCorreo());
                itemView.setSelected(posicionSeleccionada == position);
            }

        }

        @Override
        public void onClick(View view) {
            if (onContactoClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onContactoClickListener.onContactoClick(listaDeContactos.get(position));

                    // Actualizar la posición seleccionada
                    posicionSeleccionada = position;

                    // Notificar al adaptador sobre el cambio en la selección
                    notifyDataSetChanged();
                }
            }
        }
    }
}
