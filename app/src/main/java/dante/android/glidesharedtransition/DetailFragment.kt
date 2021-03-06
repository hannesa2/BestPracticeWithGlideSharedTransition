package dante.android.glidesharedtransition

import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.Fade
import android.transition.TransitionSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_picture_detail.*
import kotlin.concurrent.thread

class DetailFragment : Fragment() {

    private val image: Image by lazy {
        arguments!!.getParcelable<Image>(ARG_IMAGE) as Image
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_picture_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        load(detailImage, image)
        activity?.window?.sharedElementEnterTransition = TransitionSet()
                .addTransition(ChangeImageTransform())
                .addTransition(ChangeBounds())
        activity?.window?.enterTransition = Fade().apply {
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
            excludeTarget(R.id.action_bar_container, true)
        }
        detailImage.setOnLongClickListener {
            saveFile(image.originalUrl)
            return@setOnLongClickListener true
        }
    }

    private fun saveFile(url: String) {
        thread {
            val file = Glide.with(this).download(url).submit().get()
            Toast.makeText(context, "File saved into ${file.path}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun load(imageView: ImageView, image: Image) {
        progress.show()
        imageView.transitionName = image.thumbUrl
        val thumbnail = Glide.with(this)
                .asBitmap()
                .onlyRetrieveFromCache(true)
                .load(image.thumbUrl)
                .listener(getDelayedTransitionListener())
        imageView.load(
                if (image.originalUrl.isEmpty()) image.thumbUrl else image.originalUrl,
                thumbnail = thumbnail,
                onFinished = {
                    progress?.hide()
                })
    }

    companion object {
        private const val ARG_IMAGE = "image"
        private const val ARG_TRANSITION = "position"

        fun newInstance(image: Image, showTransition: Boolean): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_IMAGE, image)
                    putBoolean(ARG_TRANSITION, showTransition)
                }
            }
        }
    }
}
