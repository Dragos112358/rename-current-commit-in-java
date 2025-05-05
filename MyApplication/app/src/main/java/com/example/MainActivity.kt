package com.example.myapplication

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout.LayoutParams
import org.json.JSONObject
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.min

data class Message(
    val sender: String,
    val receiver: String,
    val timestamp: String,
    val content: String
)
class MainActivity : AppCompatActivity() {
    companion object {
        var currentUserEmail: String? = null
    }
    private lateinit var layout: LinearLayout
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    private lateinit var sharedPreferences: SharedPreferences
    private val PREFS_NAME = "UserPrefs"
    private val USERS_KEY = "users"
    private lateinit var loadingDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        //resetAllData()
        showLoadingScreen()
        Thread.sleep(3000)
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


        // Așteaptă câteva secunde înainte de a ascunde loading screen
        Handler(Looper.getMainLooper()).postDelayed({
            // După 3 secunde, ascunde loading screen-ul
            hideLoadingScreen()

            // Aici poți adăuga codul pentru a continua logica aplicației tale
            // De exemplu, poți lansa un nou activitate sau iniția un alt proces
        }, 3000) // 3000 ms = 3 secunde
        layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            setPadding(50, 100, 50, 100)
        }

        currentUserEmail?.let {
            showHomePage(it)  // Folosește email-ul pentru a arăta pagina principală
        } ?: showInitialMenu()
        //showInitialMenu()
        setContentView(layout)
    }
    private fun showLoadingScreen() {
        loadingDialog = Dialog(this).apply {
            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(64, 64, 64, 64)
                setBackgroundColor(Color.TRANSPARENT)

                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 48f
                    setColor(Color.WHITE)
                    setStroke(2, Color.LTGRAY)
                }

                // ProgressBar cu spațiu dedesubt
                val progressBar = ProgressBar(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = 40
                    }
                    isIndeterminate = true
                }

                // Text de loading
                val loadingText = TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    text = "🔄 Loading..."
                    textSize = 20f
                    setTextColor(Color.DKGRAY)
                    typeface = Typeface.DEFAULT_BOLD
                    gravity = Gravity.CENTER
                }

                addView(progressBar)
                addView(loadingText)
            }

            setContentView(layout)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window?.setGravity(Gravity.CENTER)
            setCancelable(false)
            show()
        }
    }



    private fun hideLoadingScreen() {
        // După finalizarea încărcării, închide dialogul
        if (::loadingDialog.isInitialized && loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }
    fun getCircularBitmap(context: Context, drawableId: Int, size: Int = 200): Bitmap {
        val original = BitmapFactory.decodeResource(context.resources, drawableId)

        // Redimensionăm imaginea la dimensiunea butonului (ex: 200x200)
        val scaled = Bitmap.createScaledBitmap(original, size, size, true)

        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(scaled, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        val rect = RectF(0f, 0f, size.toFloat(), size.toFloat())
        canvas.drawOval(rect, paint)

        return output
    }


    private fun showInitialMenu() {
        layout.removeAllViews()
        val titleTextView = TextView(this).apply {
            text = "Welcome to Actify"
            textSize = 55f
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER

            // Setează un font cursiv
            typeface = Typeface.create("cursive", Typeface.NORMAL) // Folosește un font cursiv

            // Setează culoarea textului
            setTextColor(Color.parseColor("#096A09"))  // O culoare vibrantă (verde în acest caz)

            // Adăugarea unui efect de umbră pentru a face textul să iasă în evidență
            setShadowLayer(2f, 1f, 1f, Color.BLUE)  // (raza, offset pe X, offset pe Y, culoarea umbrei)

            // Adăugăm margini și setăm layout-ul
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 150)  // Marjă pentru a lăsa spațiu între titlu și butoane
            }
        }
        emailEditText = EditText(this).apply {
            hint = "Email"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        passwordEditText = EditText(this).apply {
            hint = "Password"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val registerButton = Button(this).apply {
            text = "Register"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        val alreadyHaveAccountText = TextView(this).apply {
            text = "Already have an account?"
            textSize = 25f
            gravity = Gravity.CENTER
            setTextColor(Color.DKGRAY)
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 20, 0, 10)
            }
        }
        val loginLinkText = TextView(this).apply {
            text = "Login"
            textSize = 18f
            setTextColor(Color.parseColor("#0000EE")) // Albastru tipic pentru linkuri
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG // Sublinează textul
            gravity = Gravity.CENTER
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            setOnClickListener {
                showLoginUI()
            }
        }
        val loginLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 20, 0, 10)
            }

            val infoText = TextView(this@MainActivity).apply {
                text = "Already have an account? "
                textSize = 25f
                setTextColor(Color.DKGRAY)
            }

            val loginLink = TextView(this@MainActivity).apply {
                text = "Login"
                textSize = 25f
                setTextColor(Color.parseColor("#0000EE")) // Link-style albastru
                paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
                setOnClickListener {
                    showLoginUI()
                }
            }

            addView(infoText)
            addView(loginLink)
        }
        val exitButton = Button(this).apply {
            text = "Exit"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        //layout.setBackgroundColor(Color.BLACK)
        layout.addView(titleTextView)
        //loginButton.setOnClickListener { showLoginUI() }
        registerButton.setOnClickListener { showRegisterUI() }
        exitButton.setOnClickListener {
            finish()
        }
        val inputLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            gravity = Gravity.CENTER
            setPadding(16, 32, 16, 16)
        }
        inputLayout.addView(emailEditText)
        inputLayout.addView(passwordEditText)
        inputLayout.addView(registerButton)
        inputLayout.addView(loginLayout)
        inputLayout.addView(exitButton)
        val frameLayout = FrameLayout(this).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        frameLayout.addView(inputLayout)
        val backLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.BOTTOM
        }
        //frameLayout.addView(backButton, backLayoutParams)
        layout.addView(frameLayout)
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val usersJson = sharedPreferences.getString(USERS_KEY, "{}")
                val users = JSONObject(usersJson)

                if (users.has(email)) {
                    Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
                } else {
                    users.put(email, password)
                    MainActivity.currentUserEmail = email
                    sharedPreferences.edit().putString(USERS_KEY, users.toString()).apply()
                    Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show()
                    showHomePage(email)
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun markConversationAsRead(currentUser: String, otherUser: String) {
        val prefs = getSharedPreferences("chat_read_status", MODE_PRIVATE)
        val editor = prefs.edit()

        val messages = getChatMessages(currentUser, otherUser).split("\n")
        val lastMessageTimestamp = messages.lastOrNull()?.split("|")?.getOrNull(1)

        if (lastMessageTimestamp != null) {
            editor.putString("$currentUser-$otherUser", lastMessageTimestamp)
            editor.apply()
        }
    }
    private fun showLoginUI() {
        layout.removeAllViews()
        val usersJson = sharedPreferences.getString(USERS_KEY, "{}")
        Log.d("Login", "Loaded users: $usersJson")

        emailEditText = EditText(this).apply {
            hint = "Email"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        passwordEditText = EditText(this).apply {
            hint = "Password"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }


        val loginButton = Button(this).apply {
            text = "Login"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        val backButton = Button(this).apply {
            text = "Back"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        backButton.setOnClickListener {
            showInitialMenu()
        }


        layout.addView(emailEditText)
        layout.addView(passwordEditText)
        layout.addView(loginButton)
        layout.addView(backButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            val usersJson = sharedPreferences.getString(USERS_KEY, "{}")
            val users = JSONObject(usersJson)

            if (users.has(email) && users.getString(email) == password) {
                MainActivity.currentUserEmail = email
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                showHomePage(email)

            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRegisterUI() {
        layout.removeAllViews()
        emailEditText = EditText(this).apply {
            hint = "Email"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        passwordEditText = EditText(this).apply {
            hint = "Password"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        val registerButton = Button(this).apply {
            text = "Register"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        val backButton = Button(this).apply {
            text = "Back"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        backButton.setOnClickListener {
            showInitialMenu()
        }
       val inputLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            gravity = Gravity.CENTER
            setPadding(16, 32, 16, 16)
        }
        inputLayout.addView(emailEditText)
        inputLayout.addView(passwordEditText)
        inputLayout.addView(registerButton)
        val frameLayout = FrameLayout(this).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        frameLayout.addView(inputLayout)
        val backLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.BOTTOM
        }
        frameLayout.addView(backButton, backLayoutParams)
        layout.addView(frameLayout)
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val usersJson = sharedPreferences.getString(USERS_KEY, "{}")
                val users = JSONObject(usersJson)

                if (users.has(email)) {
                    Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
                } else {
                    users.put(email, password)
                    MainActivity.currentUserEmail = email
                    sharedPreferences.edit().putString(USERS_KEY, users.toString()).apply()
                    Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show()
                    showHomePage(email)
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun transferKeys(oldEmail: String, newEmail: String) {
        val prefs = getSharedPreferences("messages", MODE_PRIVATE)
        val allMessages = prefs.all
        val editor = prefs.edit()

        for ((key, value) in allMessages) {
            Log.d("Mesaje","Mesaj: $key, $value")
            if (key.startsWith("chat_") && key.contains(oldEmail)) {
                val parts = key.removePrefix("chat_").split("_")
                if (parts.size != 2) {
                    val x= parts.size
                    Log.d("Mesaje","$x")
                    continue
                }

                var user1 = parts[0]
                var user2 = parts[1]
                user1 = if (user1 == oldEmail) newEmail else user1
                user2 = if (user2 == oldEmail) newEmail else user2
                Log.d("User","Useri: $user1, $user2")
                val newKey = "chat_${user1}_$user2"
                val oldMessages = value as? String ?: continue
                val updatedMessages = oldMessages.lines().joinToString("\n") { line ->
                    val msgParts = line.split("|")
                    if (msgParts.size == 3) {
                        Log.d("O ia pe aici","$msgParts[0], $msgParts[1]")
                        val sender = if (msgParts[0] == oldEmail) newEmail else msgParts[0]
                        "$sender|${msgParts[1]}|${msgParts[2]}"
                    } else {
                        line
                    }
                }
                editor.putString(newKey, updatedMessages)
                editor.remove(key)
            }
        }

        editor.apply()
    }

    private fun showEditProfileUI(email: String) {
        layout.removeAllViews()

        val usersJson = sharedPreferences.getString(USERS_KEY, "{}")
        val users = JSONObject(usersJson)

        val profilePrefs = getSharedPreferences("profile_data", MODE_PRIVATE)
        val currentName = profilePrefs.getString("name_$email", "")

        val title = TextView(this).apply {
            text = "Edit Profile"
            textSize = 22f
            setPadding(16, 16, 16, 16)
        }

        val nameInput = EditText(this).apply {
            hint = "Display name"
            setText(currentName)
        }

        val emailInput = EditText(this).apply {
            hint = "New email (leave unchanged if not editing)"
            setText(email)
        }

        val passwordInput = EditText(this).apply {
            hint = "New password (leave blank if unchanged)"
        }

        val saveButton = Button(this).apply {
            text = "Save Changes"
        }

        val backButton = Button(this).apply {
            text = "Back"
        }

        saveButton.setOnClickListener {
            val newName = nameInput.text.toString().trim()
            val newEmail = emailInput.text.toString().trim()
            val newPassword = passwordInput.text.toString().trim()

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Name and email cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newEmail != email && users.has(newEmail)) {
                Toast.makeText(this, "This email is already in use", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val passwordToStore = if (newPassword.isNotEmpty()) newPassword else users.getString(email)
            if (newEmail != email) {
                users.remove(email)
            }
            users.put(newEmail, passwordToStore)
            sharedPreferences.edit().putString(USERS_KEY, users.toString()).apply()
            profilePrefs.edit().remove("name_$email").apply()
            profilePrefs.edit().putString("name_$newEmail", newName).apply()
            transferKeys(email, newEmail)

            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
            showHomePage(newEmail)
        }

        backButton.setOnClickListener {
            showHomePage(email)
        }

        layout.addView(title)
        layout.addView(nameInput)
        layout.addView(emailInput)
        layout.addView(passwordInput)
        layout.addView(saveButton)
        layout.addView(backButton)
    }
    private fun showHomePage(email: String) {
        layout.removeAllViews()

        val rootLayout = FrameLayout(this).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }

        // Top circular buttons (left and right)
        val topButtonsLayout = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }
        val leftCircularBitmap = getCircularBitmap(this, R.drawable.menu, 150)

// Creează butonul din stânga cu imagine rotundă
        val leftCircleButton = ImageButton(this).apply {
            layoutParams = FrameLayout.LayoutParams(150, 150, Gravity.START or Gravity.TOP).apply {
                marginStart = 32
                topMargin = 32
            }

            setImageBitmap(leftCircularBitmap)

            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.TRANSPARENT)
            }

            scaleType = ImageView.ScaleType.CENTER_CROP
            adjustViewBounds = true
            setPadding(0, 0, 0, 0)

            val rotationAnimator = ObjectAnimator.ofFloat(this, View.ROTATION, 0f, 360f).apply {
                duration = 1000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.RESTART
            }

            /*setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Pornește animația la atingere
                        if (!rotationAnimator.isRunning) {
                            rotationAnimator.start()
                        }
                        true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        // Oprește animația când utilizatorul ridică degetul
                        rotationAnimator.cancel()
                        this.rotation = 0f
                        false
                    }
                    else -> false
                }
            }*/
                setOnClickListener {
                    val dialog = Dialog(context)
                    dialog.window?.apply {
                        requestFeature(Window.FEATURE_NO_TITLE)
                        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        attributes.windowAnimations = android.R.style.Animation_Dialog
                        setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL)
                    }

                    val layout = LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(40, 40, 40, 40)
                        background = GradientDrawable().apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = 32f
                            setColor(Color.parseColor("#FFFFFF")) // Fundal alb
                            setStroke(2, Color.parseColor("#DDDDDD")) // Margine subtilă
                        }
                        elevation = 16f // Umbra
                    }

                    val menuItems = listOf(
                        "📝  Add Notes" to {
                            showAddNotesUI(email) // Apelăm funcția pentru a adăuga note
                        },
                        "📖  View My Notes" to {
                            showViewNotesUI(email) // Apelăm funcția pentru a vizualiza notele
                        },
                        "💬  Discuss" to {
                            showUserListForChat(email) // Apelăm funcția pentru a discuta
                        }
                    )
                    for ((text, action) in menuItems) {
                        val item = TextView(context).apply {
                            this.text = text
                            textSize = 18f
                            setTextColor(Color.BLACK)
                            setPadding(32, 32, 32, 32)
                            typeface = Typeface.DEFAULT_BOLD
                            setOnClickListener {
                                action()
                                dialog.dismiss()
                            }
                            background = ColorDrawable(Color.TRANSPARENT)
                        }
                        layout.addView(item)

                        // separator subțire între iteme
                        layout.addView(View(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                1
                            ).apply {
                                setMargins(32, 0, 32, 0)
                            }
                            setBackgroundColor(Color.parseColor("#EEEEEE"))
                        })
                    }

                    dialog.setContentView(layout)

                    // Poziționează sub top bar
                    dialog.window?.attributes = dialog.window?.attributes?.apply {
                        gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                        y = 180
                    }
                    dialog.show()
                }
            }
        val circularBitmap = getCircularBitmap(this, R.drawable.poza)
        val rightCircleButton = ImageButton(this).apply {
            layoutParams = FrameLayout.LayoutParams(200, 200, Gravity.END or Gravity.TOP).apply {
                marginEnd = 32
                topMargin = 32
            }

            setImageBitmap(circularBitmap) // setează imaginea rotundă

            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.TRANSPARENT)
            }

            scaleType = ImageView.ScaleType.CENTER_CROP
            adjustViewBounds = true
            setPadding(0, 0, 0, 0)
            setOnClickListener {
                val dialog = Dialog(context)
                dialog.window?.apply {
                    requestFeature(Window.FEATURE_NO_TITLE)
                    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    attributes.windowAnimations = android.R.style.Animation_Dialog
                    setGravity(Gravity.TOP or Gravity.END)
                }

                val layout = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(40, 40, 40, 40)
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = 32f
                        setColor(Color.parseColor("#FFFFFF")) // Fundal alb
                        setStroke(2, Color.parseColor("#DDDDDD")) // Margine subtilă
                    }
                    elevation = 16f
                }

                val menuItems = listOf(
                    "👤  Edit Profile" to {
                        showEditProfileUI(email)  // Funcția ta existentă
                    },
                    "⚙️  Settings" to {
                        showSettingsUI()
                        //Toast.makeText(context, "Settings coming soon!", Toast.LENGTH_SHORT).show()
                        // Poți adăuga aici o funcție pentru setări, dacă ai una
                    },
                    "🚪  Logout" to {
                        currentUserEmail = null
                        showInitialMenu() // Revine la meniul inițial
                        dialog.dismiss()
                    }
                )


                for ((text, action) in menuItems) {
                    val item = TextView(context).apply {
                        this.text = text
                        textSize = 18f
                        setTextColor(Color.BLACK)
                        setPadding(32, 32, 32, 32)
                        typeface = Typeface.DEFAULT_BOLD
                        setOnClickListener {
                            action()
                            dialog.dismiss()
                        }
                        background = ColorDrawable(Color.TRANSPARENT)
                    }
                    layout.addView(item)

                    layout.addView(View(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1
                        ).apply {
                            setMargins(32, 0, 32, 0)
                        }
                        setBackgroundColor(Color.parseColor("#EEEEEE"))
                    })
                }

                dialog.setContentView(layout)

                dialog.window?.attributes = dialog.window?.attributes?.apply {
                    gravity = Gravity.TOP or Gravity.END
                    y = 180
                    x = 32
                }

                dialog.show()
            }
        }



        topButtonsLayout.addView(leftCircleButton)
        topButtonsLayout.addView(rightCircleButton)

        // Main center layout
        val centerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(32, 32, 32, 32)
        }

        val welcomeText = TextView(this).apply {
            text = "Welcome, $email!"
            textSize = 24f
            gravity = Gravity.CENTER
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = 40
            }
        }

        //val addNotesButton = Button(this).apply { text = "Add Notes for Myself" }
        //val viewNotesButton = Button(this).apply { text = "View My Notes" }
        //val discussButton = Button(this).apply { text = "Discuss" }
        val editProfileButton = Button(this).apply { text = "Edit Profile" }
        val toggleThemeButton = Button(this).apply { text = "Toggle Dark/Light Mode" }
        val logoutButton = Button(this).apply { text = "Logout" }

        // Button actions
        toggleThemeButton.setOnClickListener {
            val currentMode = AppCompatDelegate.getDefaultNightMode()
            AppCompatDelegate.setDefaultNightMode(
                if (currentMode == AppCompatDelegate.MODE_NIGHT_YES)
                    AppCompatDelegate.MODE_NIGHT_NO
                else
                    AppCompatDelegate.MODE_NIGHT_YES
            )
            recreate()
        }

        editProfileButton.setOnClickListener { showEditProfileUI(email) }
        //discussButton.setOnClickListener { showUserListForChat(email) }
        logoutButton.setOnClickListener { showInitialMenu() }
        //addNotesButton.setOnClickListener { showAddNotesUI(email) }
        //viewNotesButton.setOnClickListener { showViewNotesUI(email) }

        // Add buttons to center layout
        centerLayout.addView(welcomeText)
        //centerLayout.addView(addNotesButton)
        //centerLayout.addView(viewNotesButton)
        //centerLayout.addView(discussButton)
        centerLayout.addView(editProfileButton)
        centerLayout.addView(toggleThemeButton)
        centerLayout.addView(logoutButton)

        // Add everything to root layout
        rootLayout.addView(centerLayout)
        rootLayout.addView(topButtonsLayout)

        layout.addView(rootLayout)
    }
    private fun showUserListForChat(email: String) {
        layout.removeAllViews()
        val usersJson = sharedPreferences.getString(USERS_KEY, "{}")
        val users = JSONObject(usersJson)
        val originalUserList = mutableListOf<String>()
        val lista_useri_necititi = getUsersWithUnreadMessages(email)
        Log.d("Lista useri necititi", "User List: $lista_useri_necititi")
        val userList = mutableListOf<String>()
        for (key in users.keys()) {
            if (key != email) {
                originalUserList.add(key)
                if (lista_useri_necititi.contains(key)) {
                    userList.add("$key (nou)")
                } else {
                    userList.add(key)
                }
            }
        }

        Log.d("showUserListForChat", "User List: $userList")

        if (userList.isEmpty()) {
            Toast.makeText(this, "No users available for chat", Toast.LENGTH_SHORT).show()
        } else {
            val userListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
            val userListView = ListView(this).apply {
                adapter = userListAdapter
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f
                )
            }
            userListView.setOnItemClickListener { _, _, position, _ ->
                val selectedUser = userList[position]
                showChatUI(email, selectedUser)  // Începe chat-ul cu utilizatorul selectat
            }
            layout.addView(userListView)
            val bottomLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            }
            val backButton = Button(this).apply {
                text = "Back"
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            }

            backButton.setOnClickListener {
                showHomePage(email)
            }
            bottomLayout.addView(backButton)
            layout.addView(bottomLayout)
        }
    }
    private fun showChatUI(email: String, selectedUser: String) {
        layout.removeAllViews()
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        }
        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }
        val chatLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }
        val chatMessages = getChatMessages(email, selectedUser).split("\n")
        for (message in chatMessages) {
            val parts = message.split("|")
            if (parts.size < 3) continue

            val senderEmail = parts[0]
            val timestamp = parts[1]
            val content = parts[2]

            val isCurrentUser = senderEmail == email
            val messageLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 8, 16, 8)
                }
                gravity = if (isCurrentUser) Gravity.END else Gravity.START
            }
            val bubbleContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Mesajul propriu-zis
            val messageTextView = TextView(this).apply {
                text = content
                textSize = 16f
                setPadding(24, 16, 24, 16)
                background = GradientDrawable().apply {
                    cornerRadius = 40f
                    setColor(if (isCurrentUser) Color.parseColor("#DCF8C6") else Color.WHITE)
                }
                setTextColor(Color.BLACK)
            }

            // Textul de sub mesaj: ora + sender (daca e altcineva)
            val timestampTextView = TextView(this).apply {
                text = if (!isCurrentUser) "$senderEmail · $timestamp" else timestamp
                textSize = 12f
                setTextColor(Color.GRAY)
                setPadding(16, 4, 16, 8)
                gravity = Gravity.END
            }
            bubbleContainer.addView(messageTextView)
            bubbleContainer.addView(timestampTextView)
            messageLayout.addView(bubbleContainer)
            chatLayout.addView(messageLayout)
        }

        scrollView.addView(chatLayout)
        val messageInputLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            gravity = Gravity.CENTER_VERTICAL
        }
        val messageEditText = EditText(this).apply {
            hint = "Enter your message"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) // Lățimea este flexibilă, iar înălțimea se ajustează automat
            setPadding(16, 16, 16, 16)
        }

        val sendButton = Button(this).apply {
            //text = "Send"
            layoutParams = LinearLayout.LayoutParams(200, 100) // Dimensiune fixă pentru buton (lățime și înălțime)
            setBackgroundResource(R.drawable.sendbutton) // Setează imaginea butonului
            setPadding(16, 16, 16, 16) // Setează padding pentru buton
        }
        messageInputLayout.addView(messageEditText)
        messageInputLayout.addView(sendButton)
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString()
            if (message.isNotEmpty()) {
                saveChatMessage(email, selectedUser, message)
                messageEditText.text.clear()
                showChatUI(email, selectedUser)
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
        val backButton = Button(this).apply {
            text = "Back"
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        backButton.setOnClickListener {
            showUserListForChat(email)
        }
        mainLayout.addView(scrollView)
        mainLayout.addView(messageInputLayout)
        mainLayout.addView(backButton)
        layout.addView(mainLayout)
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun getAllMessages(): Map<String, String> {
        val prefs = sharedPreferences
        val allMessages = prefs.all
        val chatMessages = mutableMapOf<String, String>()
        for ((key, value) in allMessages) {
                val messages = value as? String ?: continue
                chatMessages[key] = messages
        }
        return chatMessages
    }
    private fun generateChatKey(user1: String, user2: String): String {
        val (emailA, emailB) = if (user1 < user2) Pair(user1, user2) else Pair(user2, user1)
        return "chat_${emailA}_${emailB}"
    }
    private fun saveChatMessage(sender: String, receiver: String, message: String) {
        val chatKey = generateChatKey(sender, receiver)
        val currentChat = sharedPreferences.getString(chatKey, "")

        val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val fullMessage = "$sender|$timestamp|$message"

        val updatedChat = if (currentChat.isNullOrEmpty()) fullMessage else "$currentChat\n$fullMessage"

        sharedPreferences.edit().putString(chatKey, updatedChat).apply()
    }

    private fun getChatMessages(user1: String, user2: String): String {
        val chatKey = generateChatKey(user1, user2)
        return sharedPreferences.getString(chatKey, "") ?: "No messages yet."
    }
    fun getUserEmail(): String {
        val prefs = getSharedPreferences("chat_prefs", MODE_PRIVATE)
        return prefs.getString("user_email", "default_email@example.com") ?: "default_email@example.com"
    }
    override fun onDestroy() {
        super.onDestroy()
        val userId = getUserEmail()
        saveLastLoginTime(userId)
    }
    fun saveLastLoginTime(userId: String) {
        val currentTimestamp = System.currentTimeMillis().toString()
        val prefs = getSharedPreferences("chat_prefs", MODE_PRIVATE)
        prefs.edit().putString("last_login_time_$userId", currentTimestamp).apply()
    }
    fun getUsersWithUnreadMessages(userId: String): List<String> {
        val prefs = getSharedPreferences("chat_prefs", MODE_PRIVATE)
        val usersWithUnreadMessages = mutableListOf<String>()

        val usersJson = prefs.getString("users", "{}")
        val users = JSONObject(usersJson)

        val lastLoginTime = prefs.getString("last_login_time_$userId", "0")?.toLong() ?: 0

        for (key in users.keys()) {
            if (key != userId) {
                val chatKey = "chat_${key}_$userId"
                val chatMessages = prefs.getString(chatKey, "") ?: ""

                val messages = chatMessages.split("\n")
                for (message in messages) {
                    val parts = message.split("|")
                    if (parts.size >= 3) {
                        val timestamp = parts[1].toLong()
                        if (timestamp > lastLoginTime) {
                            usersWithUnreadMessages.add(key)
                            break
                        }
                    }
                }
            }
        }

        return usersWithUnreadMessages
    }



    private fun showViewNotesUI(email: String) {
        layout.removeAllViews()

        val notesTextView = TextView(this).apply {
            text = "Your Notes:\n" + getSavedNotesForUser(email)
            textSize = 18f
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        val backButton = Button(this).apply {
            text = "Back"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        backButton.setOnClickListener {
            showHomePage(email)
        }

        layout.addView(notesTextView)
        layout.addView(backButton)
    }
    private fun getSavedNotesForUser(email: String): String {
        val savedNotes = sharedPreferences.getString(email, "")
        return savedNotes ?: "No notes available."
    }
    private fun showAddNotesUI(email: String) {
        layout.removeAllViews()

        val noteEditText = EditText(this).apply {
            hint = "Enter your note here"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        val saveButton = Button(this).apply {
            text = "Save Note"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        val backButton = Button(this).apply {
            text = "Back"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        backButton.setOnClickListener {
            showHomePage(email)
        }

        layout.addView(noteEditText)
        layout.addView(saveButton)
        layout.addView(backButton)

        saveButton.setOnClickListener {
            val note = noteEditText.text.toString()

            if (note.isNotEmpty()) {
                val savedNotes = sharedPreferences.getString(email, "")
                val updatedNotes = if (savedNotes.isNullOrEmpty()) {
                    note
                } else {
                    "$savedNotes\n$note"
                }
                sharedPreferences.edit().putString(email, updatedNotes).apply()

                Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show()
                showHomePage(email)
            } else {
                Toast.makeText(this, "Please write something!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showSettingsUI() {
        // Golește complet layout-ul principal
        layout.removeAllViews()

        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#F5F5F5"))

            val settingsLayout = LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                setPadding(48, 64, 48, 48)
                setBackgroundColor(Color.parseColor("#F5F5F5"))

                // Titlu
                val settingsTitle = TextView(context).apply {
                    text = "⚙️ Setări"
                    textSize = 26f
                    setTextColor(Color.DKGRAY)
                    typeface = Typeface.DEFAULT_BOLD
                    gravity = Gravity.CENTER
                    setPadding(0, 0, 0, 48)
                }
                addView(settingsTitle)

                // Listă de setări
                val settingItems = listOf(
                    "🔔  Notifications" to { Toast.makeText(this@MainActivity, "Notificări", Toast.LENGTH_SHORT).show() },
                    "🌗  Dark/Light Mode" to {
                        val currentMode = AppCompatDelegate.getDefaultNightMode()
                        AppCompatDelegate.setDefaultNightMode(
                            if (currentMode == AppCompatDelegate.MODE_NIGHT_YES)
                                AppCompatDelegate.MODE_NIGHT_NO
                            else
                                AppCompatDelegate.MODE_NIGHT_YES
                        )
                        recreate() // reîncarcă activitatea pentru a aplica tema
                    },
                    "🔒  Change Password" to { Toast.makeText(this@MainActivity, "Schimbă parola", Toast.LENGTH_SHORT).show() },
                    "📄  Privacy Policy" to { Toast.makeText(this@MainActivity, "Privacy Policy", Toast.LENGTH_SHORT).show() }
                )

                for ((text, action) in settingItems) {
                    val item = LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL
                        background = GradientDrawable().apply {
                            cornerRadius = 32f
                            setColor(Color.WHITE)
                            setStroke(2, Color.LTGRAY)
                        }
                        setPadding(40, 40, 40, 40)
                        setOnClickListener { action() }

                        val textView = TextView(context).apply {
                            this.text = text
                            textSize = 18f
                            setTextColor(Color.BLACK)
                            typeface = Typeface.SANS_SERIF
                        }

                        addView(textView)
                    }

                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = 24
                    }

                    addView(item, params)
                }
            }

            addView(settingsLayout)
        }
        // Adaugă totul în layout-ul principal deja existent
        layout.addView(scrollView)
    }

}

