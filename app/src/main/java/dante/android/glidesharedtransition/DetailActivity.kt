package dante.android.glidesharedtransition

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_detail.*

const val ARG_POSITION = "transPosition"
const val ARG_DATA = "data"

class DetailActivity : AppCompatActivity() {

    private lateinit var adapter: DetailPagerAdapter

    private val transPosition: Int by lazy { intent.getIntExtra(ARG_POSITION, 0) }
    private val images: MutableList<Image>? by lazy { intent.getParcelableArrayListExtra<Image>(ARG_DATA)?.toMutableList() }
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        currentPosition = transPosition
        supportPostponeEnterTransition()
        images?.let {
            adapter = DetailPagerAdapter(it, fragmentManager = supportFragmentManager)
            initViewPager(adapter)
        }
    }

    private fun initViewPager(adapter: DetailPagerAdapter) {
        pager.adapter = adapter
        pager.currentItem = transPosition
        pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
            }
        })
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }


    override fun supportFinishAfterTransition() {
        if (transPosition == currentPosition) {
            super.supportFinishAfterTransition()
        } else {
            finish()
        }
    }

    inner class DetailPagerAdapter(
            private val images: List<Image>,
            fragmentManager: FragmentManager
    ) : FragmentStatePagerAdapter(fragmentManager) {

        override fun getItem(position: Int): Fragment {
            return DetailFragment.newInstance(
                    images[position], transPosition == position
            )
        }

        override fun getCount(): Int = images.size
    }
}
