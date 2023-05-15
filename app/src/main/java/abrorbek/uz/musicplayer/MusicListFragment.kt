package abrorbek.uz.musicplayer

import abrorbek.uz.musicplayer.databinding.FragmentMusicListBinding
import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.navigation.fragment.findNavController
import com.abdurashidov.musicplayer.models.AudioVisualizer
import com.abdurashidov.musicplayer.models.Music
import com.abdurashidov.musicplayer.models.MyData
import java.util.ArrayList
import kotlin.time.Duration.Companion.milliseconds

class MusicListFragment : Fragment() {

    private lateinit var binding: FragmentMusicListBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var handler: Handler
    private lateinit var musicList: ArrayList<Music>
    private lateinit var audioVisualizer: AudioVisualizer
    private var index = -2
    companion object {
        var musicList = ArrayList<Music>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        musicList = ArrayList()
        musicList.addAll(MyData.musicList)
        mediaPlayer = MyData.path!!
        index = MyData.index


    }
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicListBinding.inflate(layoutInflater)



        binding.musicTitle.text = musicList[index].author
        // play/pause buttons
        binding.play.setOnClickListener {
            if (MyData.path!!.isPlaying) {
                binding.play.setIconResource(R.drawable.ic_play)
                MyData.path!!.pause()
            } else {
                binding.play.setIconResource(R.drawable.ic_pause)
                MyData.path!!.start()
            }
        }

        //Last button click
        binding.back.setOnClickListener {
            mediaPlayer.stop()
            index = --index
            if (index <= -1) index = MusicHomeFragment.musicList.size - 1
            mediaPlayer = MediaPlayer.create(
                binding.root.context,
                Uri.parse(MusicHomeFragment.musicList[index].musicPath)
            )
            MyData.path = mediaPlayer
            binding.musicName.text = MusicHomeFragment.musicList[index].title
            binding.musicTitle.text = MusicHomeFragment.musicList[index].author
            val duration = MyData.path!!.duration.milliseconds
            binding.timeEnd.text =
                "${duration.inWholeMinutes}:${duration.inWholeSeconds - duration.inWholeMinutes * 60}"
            binding.play.setIconResource(R.drawable.ic_pause)
            mediaPlayer.start()
        }

        //Next button click
        binding.next.setOnClickListener {
            mediaPlayer.stop()
            index = ++index
            if (index >= MusicHomeFragment.musicList.size) index = 0
            mediaPlayer = MediaPlayer.create(
                binding.root.context,
                Uri.parse(MusicHomeFragment.musicList[index].musicPath)
            )
            MyData.path = mediaPlayer
            binding.musicName.text = MusicHomeFragment.musicList[index].title
            binding.musicTitle.text = MusicHomeFragment.musicList[index].author
            val duration = MyData.path!!.duration.milliseconds
            binding.timeEnd.text =
                "${duration.inWholeMinutes}:${duration.inWholeSeconds - duration.inWholeMinutes * 60}"
            binding.play.setIconResource(R.drawable.ic_pause)
            mediaPlayer.start()
        }

        //Duration
        val duration = MyData.path!!.duration.milliseconds
        binding.timeEnd.text =
            "${duration.inWholeMinutes}:${duration.inWholeSeconds - duration.inWholeMinutes * 60}"

        //seekBar set Music
        binding.seekbarr.max = MyData.path?.duration!!
        binding.seekbarr.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    MyData.path!!.seekTo(progress)
                }


                val currentTime = MyData.path!!.currentPosition
                if (currentTime.toString().length > 3) {
                    val second =
                        currentTime.toString().subSequence(0, currentTime.toString().length - 3)
                    val minut = second.toString().toInt() / 60
                    val currentSecond = second.toString().toInt() - minut * 60

                    if (minut.toString().length == 0) {
                        if (currentSecond.toString().length == 1) {
                            binding.timeStart.text = "00:0$currentSecond"
                        } else if (currentSecond.toString().length == 2) {
                            binding.timeStart.text = "00:$currentSecond"
                        }
                    } else if (minut.toString().length == 2) {
                        if (currentSecond.toString().length == 1) {
                            binding.timeStart.text = "00:0$currentSecond"
                        } else if (currentSecond.toString().length == 2) {
                            binding.timeStart.text = "00:$currentSecond"
                        }
                    } else if (minut.toString().length == 1) {
                        if (currentSecond.toString().length == 1) {
                            binding.timeStart.text = "0$minut:0$currentSecond"
                        } else if (currentSecond.toString().length == 2) {
                            binding.timeStart.text = "0$minut:$currentSecond"
                        }
                    } else if (minut.toString().length == 2) {
                        if (currentSecond.toString().length == 1) {
                            binding.timeStart.text = "$minut:0$currentSecond"
                        } else if (currentSecond.toString().length == 2) {
                            binding.timeStart.text = "$minut:$currentSecond"
                        }
                    }
                }

                previousMusic()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        seekBarChanged()

        //back Fragment
        binding.backFragment.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }


    fun seekBarChanged(){
        handler= Handler(Looper.getMainLooper())
        handler.postDelayed(runnable, 300)
    }

    val runnable=object : Runnable{
        override fun run() {
            binding.seekbarr.progress=MyData.path!!.currentPosition
            handler.postDelayed(this, 300)
        }
    }




    private fun previousMusic(){
        if (mediaPlayer!=null){
            mediaPlayer!!.setOnCompletionListener(object : MediaPlayer.OnCompletionListener{
                @SuppressLint("SetTextI18n")
                override fun onCompletion(mp: MediaPlayer?) {
                    mediaPlayer.stop()
                    ++index
                    if (index >= musicList.size) index = 0
                    mediaPlayer=MediaPlayer.create(binding.root.context, Uri.parse(musicList[index].musicPath))
                    mediaPlayer.start()
                    MyData.path=mediaPlayer
                    binding.musicName.text=musicList[index].title
                    binding.musicTitle.text=musicList[index].author
                    val duration = MyData.path!!.duration.milliseconds
                    binding.timeEnd.text =
                        "${duration.inWholeMinutes}:${duration.inWholeSeconds - duration.inWholeMinutes * 60}"
                    MyData.index=index
                    MyData.path=mediaPlayer
                }
            })
        }
    }
    fun secunt(sec:Int):String {
        var str = ""

        val sek = sec / 1000
        val soat = sek / 3600
        val seKQ = sek % 3600
        val min = seKQ / 60
        val lsec = seKQ % 60

        str += String.format("%02d:", min)
        str += String.format("%02d", lsec)
        return str
    }
}