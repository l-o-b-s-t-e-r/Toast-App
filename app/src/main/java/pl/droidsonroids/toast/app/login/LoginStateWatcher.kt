package pl.droidsonroids.toast.app.login

import io.reactivex.subjects.BehaviorSubject
import pl.droidsonroids.toast.data.enums.LoginState

interface LoginStateWatcher {
    val loginStateSubject: BehaviorSubject<LoginState>
    val isLoggedIn: Boolean
}