package alpha.soft.weather.di.component

import alpha.soft.weather.App
import alpha.soft.weather.di.module.UserApiModule
import alpha.soft.weather.di.scope.AppScope
import alpha.soft.weather.ui.base.MainActivity
import alpha.soft.weather.ui.screens.*
import dagger.Component

@AppScope
@Component(modules = [UserApiModule::class])
interface UserApiComponent {
    fun inject(app: App)
    fun inject(activity: MainActivity)
    fun inject(activity: SplashScreen)
    fun inject(fragment: AreasScreen)
    fun inject(fragment: AllItemScreen)
    fun inject(fragment: ItemScreen)
    fun inject(fragment: UseInfoScreen)
    fun inject(fragment: CriteriaScreen)
    fun inject(fragment: AboutScreen)
    fun inject(fragment: NewsLetterScreen)
    fun inject(fragment: SoilScreen)
    fun inject(fragment: WaterCriteriaScreen)
    fun inject(fragment: ContactScreen)
    fun inject(fragment: LanguageScreen)
    fun inject(fragment: MapInfoScreen)
    fun inject(fragment: NewsScreen)
    fun inject(fragment: ConnectScreen)
    fun inject(fragment: OnBoardingScreen)
}