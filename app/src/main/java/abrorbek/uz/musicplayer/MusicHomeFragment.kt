package abrorbek.uz.musicplayer

import abrorbek.adapters.MyRvAdapter
import abrorbek.uz.musicplayer.databinding.FragmentMusicHomeBinding
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.abdurashidov.musicplayer.models.Music
import com.abdurashidov.musicplayer.models.MyData

class MusicHomeFragment : Fragment(), MyRvAdapter.RvClick {
private lateinit var binding: FragmentMusicHomeBinding
    private var REQUEST_PERMISSSION: Int = 99
    private lateinit var list: ArrayList<Music>
    private lateinit var myRvAdapter: MyRvAdapter
    private var mediaPlayer: MediaPlayer? = null
    private var index = -2
    companion object {
        var musicList = ArrayList<Music>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        musicList.addAll(requireContext().getAllAudio())
        MyData.musicList.addAll(requireContext().getAllAudio())
        musicList.sortBy { it.id }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMusicHomeBinding.inflate(layoutInflater)
        list = ArrayList()

        //Add Run Time Permission to External Storage
        chekAudioPermission()

        //get MyDataPath
        if (MyData.path != null) {
            mediaPlayer = MyData.path
            binding.playerSongName.text = musicList[MyData.index].title
            binding.playerSongAuthor.text = musicList[MyData.index].author
        }

        //Songs to RecycleView
        myRvAdapter = MyRvAdapter(musicList, this)
        binding.myRv.adapter = myRvAdapter

        //Open Player Fragment
        openPlayer()

        // play/pause buttons
        pausePlayButton()

        //Last button click
        lastButton()

        //Next button click
        nextButton()

        //Get pause/play button image
        getPausePlay()

        //Get songs number
        binding.songsNumberTv.text = musicList.size.toString()


        return binding.root
    }


    private fun getPausePlay() {
        if (MyData.path != null && MyData.path!!.isPlaying) {
            binding.playPauseImage.setImageResource(R.drawable.ic_pause)
            binding.playerSongName.text = musicList[MyData.index].title
            binding.playerSongAuthor.text = musicList[MyData.index].author
        }
    }

    private fun nextButton() {
        binding.playNext.setOnClickListener {
            if (MyData.path != null) {
                mediaPlayer!!.stop()
                index = ++index
                if (index >= musicList.size) index = 0
                mediaPlayer =
                    MediaPlayer.create(binding.root.context, Uri.parse(musicList[index].musicPath))
                MyData.path = mediaPlayer
                binding.playerSongName.text = musicList[index].title
                binding.playerSongAuthor.text = musicList[index].author
                binding.playPauseImage.setImageResource(R.drawable.ic_pause)
                mediaPlayer!!.start()
            }
        }
    }

    private fun lastButton() {
        binding.playBack.setOnClickListener {
            if (MyData.path != null) {
                mediaPlayer!!.stop()
                if (index < 0) index = musicList.size
                index = --index
                mediaPlayer =
                    MediaPlayer.create(binding.root.context, Uri.parse(musicList[index].musicPath))
                MyData.path = mediaPlayer
                binding.playerSongName.text = musicList[index].title
                binding.playerSongAuthor.text = musicList[index].author
                binding.playPauseImage.setImageResource(R.drawable.ic_pause)
                mediaPlayer!!.start()
            }
        }
    }

    private fun pausePlayButton() {
        binding.playPause.setOnClickListener {
            if (MyData.path != null) {
                if (MyData.path!!.isPlaying) {
                    binding.playPauseImage.setImageResource(R.drawable.ic_play)
                    MyData.path!!.pause()
                } else {
                    binding.playPauseImage.setImageResource(R.drawable.ic_pause)
                    MyData.path!!.start()
                }
            }
        }
    }

    private fun openPlayer() {
        binding.player.setOnClickListener {
            if (MyData.path != null) {
                val navOption = NavOptions.Builder()
                navOption.setEnterAnim(R.anim.open_player_animation)
                navOption.setPopEnterAnim(R.anim.open_player_pop_animation)
                navOption.setExitAnim(R.anim.exit_player_animation)
                navOption.setPopExitAnim(R.anim.exit_player_pop_animation)
                findNavController().navigate(R.id.musicListFragment, bundleOf(), navOption.build())
                MyData.index = index
            }
        }
    }


    @SuppressLint("Range", "Recycle")
    fun Context.getAllAudio(): java.util.ArrayList<Music> {
        val tempList = java.util.ArrayList<Music>()
        //get the external storage media storage audio uri
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        //IS_MUSIC : None-zero if the audio file is music
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"

        //sort music
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"

        //Query for external storage for music files
        val cursor: Cursor? = this.contentResolver.query(
            uri, // Uri
            null, // Projection
            selection, // Selection
            null, // Selection arguments
            sortOrder // Sort order
        )

        if (cursor != null && cursor.moveToFirst()) {
            val id: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val title: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val imageId: Int = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)
            val authorId: Int = cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)

            do {
                val audioId: Long = cursor.getLong(id)
                val audioTitle: String = cursor.getString(title)
                var imagePath: String = ""
                if (imageId != -1) {
                    imagePath = cursor.getString(imageId)
                } else {
                    imagePath = R.drawable.ic_launcher_background.toString()
                }
                val musicPath: String =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val artist = cursor.getString(authorId)

                // Add the current music to the list
                tempList.add(Music(audioId, audioTitle, imagePath, musicPath, artist))
            } while (cursor.moveToNext())
        }




        return tempList
    }


    override fun onClick(music: Music) {
        if (MyData.path != null) {
            MyData.path!!.stop()
            mediaPlayer = MediaPlayer.create(binding.root.context, Uri.parse(music.musicPath))
            MyData.path = mediaPlayer
            index = musicList.indexOf(music)
            binding.playPauseImage.setImageResource(R.drawable.ic_pause)
            binding.playerSongName.text = music.title
            binding.playerSongAuthor.text = music.author
            mediaPlayer!!.start()
        } else {
            mediaPlayer = MediaPlayer.create(binding.root.context, Uri.parse(music.musicPath))
            MyData.path = mediaPlayer
            index = musicList.indexOf(music)
            binding.playerSongName.text = music.title
            binding.playerSongAuthor.text = music.author
            binding.playPauseImage.setImageResource(R.drawable.ic_pause)
            mediaPlayer!!.start()
        }
    }


    private fun previousMusic(){
        if (mediaPlayer!=null){
            mediaPlayer!!.setOnCompletionListener(object : MediaPlayer.OnCompletionListener{
                @SuppressLint("SetTextI18n")
                override fun onCompletion(mp: MediaPlayer?) {
                    mediaPlayer!!.stop()
                    ++index
                    if (index >= musicList.size) index = 0
                    mediaPlayer=MediaPlayer.create(binding.root.context, Uri.parse(musicList[index].musicPath))
                    mediaPlayer!!.start()
                    MyData.path=mediaPlayer
                    binding.playerSongName.text=musicList[index].title
                    binding.playerSongAuthor.text=musicList[index].author
                    MyData.index=index
                    MyData.path=mediaPlayer
                }
            })
        }
    }


    private fun chekAudioPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(binding.root.context, "Audio permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            requestAudioPermissions()
        }
    }

    private fun requestAudioPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {

            val builder = AlertDialog.Builder(binding.root.context)
            builder.setMessage("Ovoz yozib olish uchun ruxsat berishingiz kerak aks holda ilova irofonni ishlata olmaydi")
            builder.setTitle("Permissions")

            builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_PERMISSSION
                )
            })
            builder.create().show()

        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSSION
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == this.REQUEST_PERMISSSION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                musicList.addAll(requireContext().getAllAudio())
                MyData.musicList.addAll(requireContext().getAllAudio())
                myRvAdapter.list = musicList
                myRvAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.my_menu, menu)

        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search for people..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Qidiruvni bajarish uchun ish kodlari
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Izlanayotgan so'zni qidirish uchun ish kodlari
                return false
            }
        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.search) {
            val searchView = activity?.findViewById<SearchView>(R.id.search)
            if (searchView != null) {
                searchView.isIconified = false
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
//        val searchView = activity?.findViewById<SearchView>(R.id.search)
//        searchView?.isIconified = true
    }

}