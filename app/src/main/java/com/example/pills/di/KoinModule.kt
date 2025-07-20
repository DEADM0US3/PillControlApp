package com.example.pills.di

import com.example.pills.pills.domain.repository.LoginRepository
import com.example.pills.pills.domain.repository.ResetPasswordRepository
import com.example.pills.pills.domain.repository.SetPasswordRepository
import com.example.pills.pills.domain.repository.SignUpRepository
import com.example.pills.pills.domain.repository.ProfileRepository
import com.example.pills.pills.domain.supabase.SupabaseClientProvider
import com.example.pills.pills.domain.use_case.ValidateEmail
import com.example.pills.pills.domain.use_case.ValidateName
import com.example.pills.pills.domain.use_case.ValidatePassword
import com.example.pills.pills.domain.use_case.ValidateTerms
import com.example.pills.pills.domain.use_case.GetUserProfile
import com.example.pills.pills.domain.use_case.UpdateUserProfile
import com.example.pills.pills.domain.use_case.UpdateProfileImage
import com.example.pills.pills.domain.use_case.UploadProfileImage
import com.example.pills.pills.domain.use_case.DeleteProfileImage
import com.example.pills.pills.domain.use_case.ValidatePhone
import com.example.pills.pills.domain.use_case.ValidateAge
import com.example.pills.pills.presentation.forgetPassword.reset.ResetPasswordViewModel
import com.example.pills.pills.presentation.forgetPassword.setNew.SetPasswordViewModel
import com.example.pills.pills.presentation.login.LoginViewModel
import com.example.pills.pills.presentation.main.MainViewModel
import com.example.pills.pills.presentation.otpVerification.OtpViewModel
import com.example.pills.pills.presentation.signUp.SignUpViewModel
import com.example.pills.pills.presentation.profile.EditProfileViewModel
import com.example.pills.homePage.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


/**
 * - This module is used to define dependencies that are instances of normal classes
 * - Register all dependencies your ViewModels need.
 */
val appModule = module {
    // Register SupabaseClientProvider as a Singleton
    single { SupabaseClientProvider.client }

    // Define a singleton for SignUpRepository
    single { SignUpRepository(get()) }

    single { ResetPasswordRepository(get()) }

    single { SetPasswordRepository(get()) }

    single { ProfileRepository(get(), androidContext()) }

    // Provide LoginRepository (Now with SupabaseClientProvider)
    single { LoginRepository(androidContext(), get() ) }


    // Define use-case dependencies
    factoryOf(::ValidateEmail)
    factoryOf(::ValidatePassword)
    factoryOf(::ValidateTerms)
    factoryOf(::ValidateName)
    factoryOf(::GetUserProfile)
    factoryOf(::UpdateUserProfile)
    factoryOf(::UpdateProfileImage)
    factoryOf(::UploadProfileImage)
    factoryOf(::DeleteProfileImage)
    factoryOf(::ValidatePhone)
    factoryOf(::ValidateAge)
}


/**
 * - This module provides all ViewModels used in the app.
 * - Every ViewModel must be declared here.
 */
val viewModelModule = module {
    // Use factoryOf instead of viewModel()
    viewModelOf(::SignUpViewModel)
    viewModelOf(::OtpViewModel)
    viewModelOf(::LoginViewModel) // Added LoginViewModel
    viewModelOf(::HomeViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::ResetPasswordViewModel)
    viewModelOf(::SetPasswordViewModel)
    viewModelOf(::EditProfileViewModel)
}