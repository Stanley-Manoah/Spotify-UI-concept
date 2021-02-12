package atomsandbots.android.spotifyvideos.registration

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import atomsandbots.android.spotifyvideos.LoginViewModel
import atomsandbots.android.spotifyvideos.R
import atomsandbots.android.spotifyvideos.databinding.FragmentMainBinding
import atomsandbots.android.spotifyvideos.ui.SplashActivity
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth


class MainFragment : Fragment() {

    companion object {
        const val TAG = "MainFragment"
        const val SIGN_IN_RESULT_CODE = 1001
    }

    // Create a VideoView variable, a MediaPlayer variable, and an int to hold the current
    // video position.
    var mMediaPlayer: MediaPlayer? = null
    var mCurrentVideoPosition = 0

    // Get a reference to the ViewModel scoped to this Fragment
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.welcomeText.text = viewModel.getFactToDisplay(requireContext())
        binding.authButton.setOnClickListener {//Go to login

            activity?.let {
                val intent = Intent(it, LoginFragment::class.java)
                it.startActivity(intent)
            }
        }
        binding.signUpBtn.setOnClickListener { //create account UI

            activity?.let {
                val intent = Intent(it, RegisterActivity::class.java)
                it.startActivity(intent)
            }
        }

        // set the absolute path of the video file which is going to be played
        binding.videoView.setVideoURI(Uri.parse("android.resource://"
                + "atomsandbots.android.spotifyvideos" + "/" + R.raw.spotify_video_bg))

        // Set the new Uri to our VideoView
        binding.videoView.requestFocus()
        // Start the VideoView
        binding.videoView.start()


        // Set an OnPreparedListener for our VideoView. For more information about VideoViews,
        // check out the Android Docs: https://developer.android.com/reference/android/widget/VideoView.html
        binding.videoView.setOnPreparedListener { mediaPlayer ->
            mMediaPlayer = mediaPlayer
            // We want our video to play over and over so we set looping to true.
            mMediaPlayer!!.isLooping = true
            // We then seek to the current position if it has been set and play the video.
            if (mCurrentVideoPosition != 0) {
                mMediaPlayer!!.seekTo(mCurrentVideoPosition)
                mMediaPlayer!!.start()
            }
        }

        return binding.root
    }


    override fun onPause() {
        super.onPause()
        // Capture the current video position and pause the video.
       // mCurrentVideoPosition = mMediaPlayer!!.currentPosition
        binding.videoView.pause()
    }

    override fun onResume() {
        super.onResume()
        // Restart the video when resuming the Activity
        binding.videoView.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // When the Activity is destroyed, release our MediaPlayer and set it to null.
        mMediaPlayer!!.release()
        mMediaPlayer = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()//check/use auth state here!

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                //User successfully signed in
                Log.i(
                        TAG,
                        "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

    /**
     * Observes the authentication state and changes the UI accordingly.
     * If there is a logged in user: (1) show a logout button and (2) display their name.
     * If there is no logged in user: show a login button
     */
    private fun observeAuthenticationState() {
        val factToDisplay = viewModel.getFactToDisplay(requireContext())

        viewModel.authenticationState.observe(viewLifecycleOwner, { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {

                    activity?.let {
                        val intent = Intent(it, SplashActivity::class.java)
                        it.startActivity(intent)
                    }
                }
                else -> {
                    binding.welcomeText.text = factToDisplay

                    binding.authButton.text = getString(R.string.login_button_text)
                    binding.authButton.setOnClickListener {
                        //launchSignInFlow()
                        activity?.let {
                            val intent = Intent(it, RegisterActivity::class.java)
                            it.startActivity(intent)
                        }

                    }
                }
            }
        })
    }


}