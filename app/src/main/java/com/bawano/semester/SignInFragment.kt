package com.bawano.semester

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.IntentSender
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bawano.semester.databinding.FragmentSignInBinding
import com.bawano.semester.utils.*
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider


class SignInFragment : Fragment() {

    private lateinit var b: FragmentSignInBinding
    private val args: SignInFragmentArgs by navArgs()

    private val ONE_TAP = 2
    private var showOneTapUI = true

    private val auth = Fp.authInstance()
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var googleSignInOneTap: ActivityResultLauncher<IntentSenderRequest>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentSignInBinding.inflate(inflater)
        animateViews()
        onViewClick()
        return b.root
    }


    private fun animateViews() {
        b.emailLayout.slideInFromLeft()
        b.etPasswordLayout.slideInFromLeft(delay = 300L)
        b.signInBtn.slideInFromDown(delay = 1000L)
        b.googleSignInButton.slideInFromDown(delay = 1300L)
        b.v1.fadeIn(delay = 1300L)
        b.v2.fadeIn(delay = 1300L)
        b.or.fadeIn(delay = 1300L)
        b.haveAccount.fadeIn(delay = 1300L)
        b.disclaimer.fadeIn(delay = 1300L)

    }

    private fun onViewClick() {
        args.email?.let { b.emailEt.setText(args.email) }
        args.password?.let {
            b.emailEt.setText(args.email)
            b.etPassword.setText(args.password)
        }
        b.haveAccount.addLinksToText(Pair("Sign In", View.OnClickListener {
            val email = b.emailEt.text.toString()
            val password = b.etPassword.text.toString()
            val action =
                SignInFragmentDirections.actionSignInFragmentToSignUpFragment(email, password)
            findNavController().navigate(action)
        }))
        b.recoverPassword.addLinksToText(Pair(b.recoverPassword.text.toString(), View.OnClickListener{beginRecover(b.emailEt.text.toString())}))
        b.disclaimer.addLinksToText(
            // TODO: privacy policy and terms and conditions
        )
        b.googleSignInButton.setOnClickListener {
            if (showOneTapUI) startOneTapSignInFlow()
        }

        b.signInBtn.setOnClickListener {
            if (isValidInfo()) login()
        }
    }

    private fun login() {
        b.progressBar.visibility = View.VISIBLE
        Fp.authInstance().signInWithEmailAndPassword(
            b.emailEt.text.toString().trim(),
            b.etPassword.text.toString()
        ).addOnCompleteListener {
            b.progressBar.visibility = View.GONE
            if (it.isSuccessful) {
                findNavController().navigate(R.id.action_signInFragment_to_nav_home)
            } else {
                it.exception?.let {e->
                    if ((e as FirebaseAuthException).errorCode == "ERROR_USER_NOT_FOUND"){
                        redirectToRegisterDialog(b.emailEt.text.toString())
                        return@addOnCompleteListener
                    }
                }
                showErrorDialog(it.exception)
            }
        }
    }


    private fun startOneTapSignInFlow() {
        b.progressBar.visibility = View.VISIBLE
        oneTapClient = Identity.getSignInClient(requireContext())
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.your_web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(requireActivity()) {
                try {
                    val intentSenderRequest: IntentSenderRequest = IntentSenderRequest
                        .Builder(it.pendingIntent.intentSender)
                        .build()
                    googleSignInOneTap.launch(intentSenderRequest)
                } catch (e: IntentSender.SendIntentException) {
                    requireContext().toast("failed to access one tap log in")
                }
            }
            .addOnFailureListener(requireActivity()) { }
    }

    private fun isValidInfo(): Boolean {
        var valid = true
        val email = b.emailEt.text.toString()
        val password = b.etPassword.text.toString()

        if (email.isEmpty()) {
            b.emailLayout.error = "Enter your email Address"
            valid = false
        }
        if (password.isEmpty()) {
            b.etPasswordLayout.error = "Enter a password"
            valid = false
        }
        if (password.length < 8) {
            b.etPasswordLayout.error = "Password too Short"
            valid = false
        }

        return valid
    }

    private fun redirectToRegisterDialog(email: String) {
        val password = b.etPassword.text.toString()
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setTitle("Account doesn't exist")
        builder.setMessage("There was no account corresponding to $email. would you like to register a new account?")
        builder.setPositiveButton(
            "Yes"
        ) { _, _ ->
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment(email, password)
            findNavController().navigate(action)
        }
        builder.setNeutralButton(
            "No"
        ) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun showRecoverPasswordDialog() {
        //alert dialog
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.recover_password)
        val linearLayout = LinearLayout(requireContext())
        val emailEt = EditText(requireContext())
        emailEt.hint = "Email"
        emailEt.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        emailEt.minEms = 16
        linearLayout.addView(emailEt)
        linearLayout.setPadding(10, 10, 10, 10)
        builder.setView(linearLayout)
        builder.setPositiveButton("Recover") { _, _->
            b.progressBar.visibility = View.VISIBLE
            val email = emailEt.text.toString().trim { it <= ' ' }
            if (email.isEmpty()) requireContext().toast("email cant be empty")
            else beginRecover(email)
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _-> dialog.dismiss() }
        builder.create().show()
    }

    private fun beginRecover(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task: Task<Void?> ->
                b.progressBar.visibility = View.INVISIBLE
                requireContext().toast(if (task.isSuccessful)"Check your Email Address" else "Please try again")

            }.addOnFailureListener { e: Exception ->
                b.progressBar.visibility = View.INVISIBLE
                showErrorDialog(e)
            }
    }


    private var passwordWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            if (charSequence.length < 8) {
                b.etPasswordLayout.error = "Password too Short"
            } else b.etPasswordLayout.error = null
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    private var emailWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            b.emailLayout.error = null
        }

        override fun afterTextChanged(editable: Editable) {}
    }

    override fun onResume() {
        super.onResume()
        b.etPassword.addTextChangedListener(passwordWatcher)
        b.emailEt.addTextChangedListener(emailWatcher)
    }

    override fun onPause() {
        super.onPause()
        b.etPassword.removeTextChangedListener(passwordWatcher)
        b.emailEt.removeTextChangedListener(emailWatcher)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleSignInOneTap =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == ONE_TAP) {
                    try {
                        val credential = oneTapClient.getSignInCredentialFromIntent(it.data)
                        val idToken = credential.googleIdToken
                        when {
                            idToken != null -> {
                                val firebaseCredential =
                                    GoogleAuthProvider.getCredential(idToken, null)
                                auth.signInWithCredential(firebaseCredential)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            findNavController().navigate(R.id.action_signInFragment_to_nav_home)
                                        } else {
                                            b.progressBar.visibility = View.GONE
                                            showErrorDialog(task.exception)
                                        }
                                    }

                            }
                            else -> {
                                requireContext().toast("No ID token! ty again")
                            }
                        }
                    } catch (e: ApiException) {
                        when (e.statusCode) {
                            CommonStatusCodes.CANCELED -> {
                                requireContext().toast("One-tap dialog was canceled.")
                                // Don't re-prompt the user.
                                showOneTapUI = false
                            }
                            CommonStatusCodes.NETWORK_ERROR -> {
                                requireContext().toast("One-tap encountered a network error.")
                                // Try again or just ignore.
                            }
                            else -> {
                                requireContext().toast("Couldn't find any credentials please manually sign up.")
                            }
                        }
                    }


                }
                b.progressBar.visibility = View.GONE

            }
    }

    private fun showErrorDialog(e: Exception?) {

        (AlertDialog.Builder(requireContext())
            .setCancelable(true)).setPositiveButton("dismiss") { dialog, _ ->
                dialog.dismiss()
            }
            .setTitle("Sign Up Failed").setMessage(
                when (e) {
                    null -> "An unknown error occurred"
                    is FirebaseNetworkException -> "Please Connect to the internet"
                    else -> Fp.getErrorString((e as FirebaseAuthException).errorCode)
                }
            ).create().show()
    }
}