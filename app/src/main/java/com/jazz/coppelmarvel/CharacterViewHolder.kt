package com.jazz.coppelmarvel

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jazz.coppelmarvel.databinding.ItemCharacterBinding
import com.squareup.picasso.Picasso

class CharacterViewHolder(view:View):RecyclerView.ViewHolder(view) {

    private val binding=ItemCharacterBinding.bind(view)

    fun bind(hero:Character){
        val ruta=hero.thumbnail.path + "." + hero.thumbnail.extension
        //Picasso.get().load(ruta).into(binding.ivCharacter)
        Glide.with(itemView).load(ruta).into(binding.ivCharacter);
        binding.tvCharacter.setText(hero.name)
        binding.tvCharacterDescription.setText(hero.description)
    }

}