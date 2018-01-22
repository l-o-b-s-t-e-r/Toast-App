package pl.droidsonroids.toast.app.photos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_photos.*
import pl.droidsonroids.toast.R
import pl.droidsonroids.toast.app.base.BaseActivity
import pl.droidsonroids.toast.app.events.EventDetailsActivity
import pl.droidsonroids.toast.app.home.MainActivity
import pl.droidsonroids.toast.app.utils.MainCategories
import pl.droidsonroids.toast.utils.Constants
import pl.droidsonroids.toast.utils.NavigationRequest
import pl.droidsonroids.toast.utils.ParentView
import pl.droidsonroids.toast.utils.consume

class PhotosActivity : BaseActivity() {
    companion object {
        private const val PHOTOS_KEY = "photos_key"
        private const val EVENT_ID_KEY = "event_id_key"
        private const val PARENT_VIEW_KEY = "parent_view_key"

        fun createIntent(context: Context, navigationRequest: NavigationRequest.Photos): Intent {
            return Intent(context, PhotosActivity::class.java)
                    .putExtra(EVENT_ID_KEY, navigationRequest.eventId)
                    .putExtra(PHOTOS_KEY, navigationRequest.photos.toTypedArray())
                    .putExtra(PARENT_VIEW_KEY, navigationRequest.parentView)
        }
    }

    private val parentEventId by lazy { intent.getLongExtra(EVENT_ID_KEY, Constants.Event.NO_EVENT_ID) }
    private val parentView by lazy { intent.getSerializableExtra(PARENT_VIEW_KEY) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)
        setSupportActionBar(photosToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> handleUpAction()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleUpAction() = consume {
        val upIntent = if (parentView == ParentView.EVENT_DETAILS) {
            val eventDetailsRequest = NavigationRequest.EventDetails(parentEventId)
            EventDetailsActivity.createIntent(this, eventDetailsRequest)
        } else {
            MainActivity.createIntent(this, MainCategories.EVENTS)
        }
        upIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(upIntent)
    }
}