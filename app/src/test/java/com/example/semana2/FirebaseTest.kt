package com.example.semana2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class FirebaseTest {
    private lateinit var auth: FirebaseAuth
    private lateinit var fakeUser: FirebaseUser

    @Before
    fun setUp() {
        auth = mock(FirebaseAuth::class.java)
        fakeUser = mock(FirebaseUser::class.java)
    }

    @Test
    fun `auth devuelve usuario`() {
        whenever(auth.currentUser).thenReturn(fakeUser)
        whenever(fakeUser.uid).thenReturn("12345")
        assertNotNull(auth.currentUser)
        assertEquals("12345", auth.currentUser?.uid)
    }

}