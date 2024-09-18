package com.example.walkypet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.walkypet.databinding.ListItemPaseadorBinding

class PaseadorAdapter(private val context: Context, private val paseadores: List<Paseador>) : BaseAdapter() {

    override fun getCount(): Int = paseadores.size

    override fun getItem(position: Int): Any = paseadores[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: ListItemPaseadorBinding
        val view: View

        if (convertView == null) {
            binding = ListItemPaseadorBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as ListItemPaseadorBinding
            view = convertView
        }

        val paseador = getItem(position) as Paseador
        binding.textViewName.text = paseador.nombre
        binding.textViewRazas.text = "Razas: ${paseador.raza}"
        binding.textViewHorario.text = "Horario: ${paseador.horario}"

        return view
    }
}
