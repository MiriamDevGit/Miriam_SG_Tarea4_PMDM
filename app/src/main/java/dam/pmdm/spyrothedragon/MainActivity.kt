package dam.pmdm.spyrothedragon

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding
import dam.pmdm.spyrothedragon.databinding.GuideBinding
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var guideBinding: GuideBinding
    private var navController: NavController? = null

    private var step = 0

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("guide_prefs", MODE_PRIVATE)
        //prefs.edit().clear().apply() //para probar
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        guideBinding = binding.guideInclude
        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.navHostFragment)

        navHostFragment?.let {
            navController = NavHostFragment.findNavController(it)
            NavigationUI.setupWithNavController(binding.navView, navController!!)
            NavigationUI.setupActionBarWithNavController(this, navController!!)
        }

        binding.navView.setOnItemSelectedListener { menuItem ->
            selectedBottomMenu(menuItem)
        }
        // inicializo guia
        initializeGuide()

        navController?.addOnDestinationChangedListener { _, destination, _ ->

            val guideCompleted = prefs.getBoolean("guide_completed", false)

            when (destination.id) {

                R.id.navigation_characters -> {

                    if (!guideCompleted) {
                        guideBinding.guideLayout.visibility = View.VISIBLE
                    }
                }

                else -> {
                    guideBinding.guideLayout.visibility = View.GONE
                }
            }

            // Flecha atrás
            when (destination.id) {
                R.id.navigation_characters,
                R.id.navigation_worlds,
                R.id.navigation_collectibles -> {
                    // En las pantallas de los tabs no mostramos la flecha atrás
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }

                else -> {
                    // En el resto de pantallas sí
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
            }
        }

    }

    private fun initializeGuide() {

        // Botón cerrar
        guideBinding.exitGuide.setOnClickListener {

            prefs.edit().putBoolean("guide_completed", true).apply()

            guideBinding.guideLayout.visibility = View.GONE
        }

        // botón siguiente
        guideBinding.btnNext.setOnClickListener {

            step++

            if (step <= 4) {
                updateGuideStep()
            } else {

                prefs.edit().putBoolean("guide_completed", true).apply()

                guideBinding.guideLayout.visibility = View.GONE
            }
        }

        // Animación texto
        guideBinding.textStep.animate()
            .alpha(1f)
            .setDuration(1000)
            .start()

        //Animación círculo
        val pulse = guideBinding.pulseImage

        val anim = AnimationUtils.loadAnimation(this, R.anim.pulse_scale)
        pulse.startAnimation(anim)

        step = 0
        updateGuideStep()
    }

    private fun updateGuideStep() {

        val params = guideBinding.pulseImage.layoutParams as FrameLayout.LayoutParams

        guideBinding.pulseImage.clearAnimation()

        //resetear margenes
        params.setMargins(0, 0, 0, 0)

        when (step) {

            0 -> {
                guideBinding.pulseImage.visibility = View.VISIBLE
                guideBinding.pulseImage.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.pulse_scale)
                )
                guideBinding.textStep.text = "Aquí podrás explorar los personajes"
                guideBinding.textStep.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.deslizar)
                )
                params.gravity = Gravity.BOTTOM or Gravity.START
                params.setMargins(-150, 0, 0, -150)
            }

            1 -> {
                guideBinding.pulseImage.visibility = View.VISIBLE
                guideBinding.pulseImage.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.pulse_scale)
                )
                guideBinding.textStep.text = "Aquí puedes ver los mundos"
                guideBinding.textStep.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.rebotar)
                )
                params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                params.setMargins(0, 0, 0, -150)
            }

            2 -> {
                guideBinding.pulseImage.visibility = View.VISIBLE
                guideBinding.pulseImage.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.pulse_scale)
                )
                guideBinding.textStep.text = "Aquí están los coleccionables"
                guideBinding.textStep.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.deslizar)
                )
                params.gravity = Gravity.BOTTOM or Gravity.END
                params.setMargins(0, 0, -150, -150)
            }

            3 -> {
                guideBinding.pulseImage.visibility = View.VISIBLE
                guideBinding.pulseImage.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.pulse_scale)
                )
                guideBinding.textStep.text = "Pulsa aquí para más información"
                guideBinding.textStep.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.rebotar)
                )
                params.gravity = Gravity.TOP or Gravity.END
                params.setMargins(0, -150, -150, 0)
            }

            4 -> {
                guideBinding.textStep.text = "Ya estás listo para usar la app"
                guideBinding.pulseImage.visibility = View.GONE
            }
        }

        guideBinding.pulseImage.layoutParams = params
    }

    private fun selectedBottomMenu(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_characters ->
                navController?.navigate(R.id.navigation_characters)

            R.id.nav_worlds ->
                navController?.navigate(R.id.navigation_worlds)

            else ->
                navController?.navigate(R.id.navigation_collectibles)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.about_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_info) {
            showInfoDialog()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun showInfoDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.title_about)
            .setMessage(R.string.text_about)
            .setPositiveButton(R.string.accept, null)
            .show()
    }
}
