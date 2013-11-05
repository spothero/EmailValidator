# EmailValidator

An Android library that will provide basic email syntax validation as well as provide suggestions for possible typos (for example, test@gamil.con would be corrected to test@gmail.com).

## Screenshots
![Typo correction suggestion](Screenshots/Screenshot\ 1.png "Typo correction suggestion")
![Basic syntax validation](Screenshots/Screenshot\ 2.png "Basic syntax validation")
![Typo correction suggestion with theming](Screenshots/Screenshot\ 3.png "Typo correction suggestion with custom colors")

## Usage
### Adding EmailValidationEditText
The `EmailValidationEditText` class is a pre-built EditText subclass that will automatically validate its own input when it loses focus (such as when the user goes from the email field to the password field).  If a syntax error is detected, a popup will appear informing the user that the email address they entered is invalid.  If a probably typo is detected, a popup will appear that allows the user to accept the suggestion or dismiss the popup.  The first thing you need to do is add this view to your layout XML:

	<RelativeLayout
            xmlns:android="http://schemas.android.com/apk/res/android"
            android:layout_width="match_parent"
            android:layout_height="match_parent" >
            
            <com.spothero.emailvalidator.EmailValidationEditText
                android:id="@+id/et_email"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="@string/email"
                android:imeOptions="actionNext"
                />
                
			<EditText
                android:id="@+id/et_password"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_below="@id/et_email"
                android:hint="@string/password"
                android:inputType="textPassword"
                android:imeOptions="actionDone|flagNoExtractUi|flagNoFullscreen"
                />
                
	</RelativeLayout>

###Customization
`EmailValidationEditText` can be customized in its look and feel as well as the error messages it will display.  The following XML attributes are available (note that these using these XML attributes requires a valid namespace.  For example, put `xmlns:custom="http://schemas.android.com/apk/res-auto"` in your top-level XML tag, then prefix the following attributes with `custom:`):

	popupBorderColor: The color of the popup's border
	
	popupFillColor: The fill color of the popup
	
	popupTitleColor: The color of the popup's title text
	
	popupSuggestionColor: The color of the email address suggestion text
	
	popupDismissButtonColor: The color of the X displayed to the right of the popup
	
	suggestionTitle: The title that appears with a suggestion (defaults to "Did you mean")
	
	defaultErrorMessage: The default message that will appear on syntax errors (defaults to "Please enter a valid email address")
	
	invalidSyntaxError: The message that appears on syntax errors (defaults to the contents of defaultErrorMessage)
	
	invalidUsernameError: The message that appears when the username portion of the email address contains an error (defaults to the contents of defaultErrorMessage)
	
	invalidDomainError: The message that appears when the domain portion of the email address contains an error (defaults to the contents of defaultErrorMessage)
	
	invalidTLDError: The message that appears when the TLD portion of the email address contains an error (defaults to the contents of defaultErrorMessage)

## Getting the Library
EmailValidator is fully maven/gradle compatible.  If you're using the awesome gradle build system, just add the following line to the dependencies block of your build.gradle file:

	dependencies {
		compile 'com.spothero.emailvalidator:EmailValidator:1.0.0@aar'
    }

If you aren't yet using gradle, this library can be downloaded from GitHub and used as an Android Library project.    

## Apps Using this Library
This library is used in our own [SpotHero](https://play.google.com/store/apps/details?id=com.spothero.spothero "SpotHero") app. If you would like to see your app listed here as well, let us know you're using it!

## License
EmailValidator is released under the Apache 2.0 license.