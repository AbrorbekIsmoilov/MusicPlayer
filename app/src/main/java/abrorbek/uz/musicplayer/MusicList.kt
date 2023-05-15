package abrorbek.uz.musicplayer

import abrorbek.adapters.MyRvAdapter
import abrorbek.uz.musicplayer.databinding.ActivityMusicListBinding
import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MusicList : AppCompatActivity(){
    private val binding by lazy {ActivityMusicListBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }

}