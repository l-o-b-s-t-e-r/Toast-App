package pl.droidsonroids.toast.app.speakers

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_speakers.*
import pl.droidsonroids.toast.app.base.BaseFragment
import pl.droidsonroids.toast.app.home.MainActivity
import pl.droidsonroids.toast.app.utils.LazyLoadingScrollListener
import pl.droidsonroids.toast.databinding.FragmentSpeakersBinding
import pl.droidsonroids.toast.utils.Constants
import pl.droidsonroids.toast.viewmodels.speaker.SpeakersViewModel

class SpeakersFragment : BaseFragment() {

    private lateinit var speakersViewModel: SpeakersViewModel

    private var speakersDisposable: Disposable? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        speakersViewModel = ViewModelProviders.of(this, viewModelFactory)[SpeakersViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentSpeakersBinding.inflate(inflater, container, false)
        binding.speakersViewModel = speakersViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showSearchMenuItemWithAnimation()
        setupRecyclerView()
        showSearchMenuItemWithAnimation()
    }

    private fun showSearchMenuItemWithAnimation() {
        animateViewByY(Constants.SearchMenuItem.SHOWN_OFFSET)
    }

    private fun hideSearchMenuItemWithAnimation() {
        animateViewByY(Constants.SearchMenuItem.HIDDEN_OFFSET)
    }

    private fun animateViewByY(offset: Float) {
        (activity as MainActivity).animateSearchButton(offset)
    }

    private fun setupRecyclerView() {
        with(speakersRecyclerView) {
            val speakersAdapter = SpeakersAdapter()
            adapter = speakersAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(SpeakerItemDecoration(context.applicationContext))
            addOnScrollListener(LazyLoadingScrollListener {
                speakersViewModel.loadNextPage()
            })

            subscribeToSpeakersChange(speakersAdapter)
        }
    }

    private fun subscribeToSpeakersChange(speakersAdapter: SpeakersAdapter) {
        speakersDisposable = speakersViewModel.speakersSubject
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(speakersAdapter::setData)
    }

    override fun onDestroyView() {
        hideSearchMenuItemWithAnimation()
        speakersDisposable?.dispose()
        super.onDestroyView()
    }
}