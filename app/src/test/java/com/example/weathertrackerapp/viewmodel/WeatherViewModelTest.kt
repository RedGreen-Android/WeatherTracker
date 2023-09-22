package com.example.weathertrackerapp.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.weathertrackerapp.getOrAwaitValue
import com.example.weathertrackerapp.model.Clouds
import com.example.weathertrackerapp.model.Coord
import com.example.weathertrackerapp.model.Main
import com.example.weathertrackerapp.model.Sys
import com.example.weathertrackerapp.model.Weather
import com.example.weathertrackerapp.model.WeatherResponse
import com.example.weathertrackerapp.model.Wind
import com.example.weathertrackerapp.repository.UserLocationImpl
import com.example.weathertrackerapp.repository.WeatherRepository
import com.example.weathertrackerapp.util.Resource
import com.example.weathertrackerapp.util.SharedPreference
import com.example.weathertrackerapp.util.Status
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response
import java.io.IOException


@RunWith(MockitoJUnitRunner::class)
class WeatherViewModelTest {

    // Mocks
    @Mock
    private lateinit var repository: WeatherRepository

    @Mock
    private lateinit var userLocation: UserLocationImpl

    @Mock
    private lateinit var sharedPreferences: SharedPreference

    @Mock
    private lateinit var context: Context

    // Set up the test coroutine dispatcher
    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = TestCoroutineDispatcher()

    // Set up the test coroutine scope
    @OptIn(ExperimentalCoroutinesApi::class)
    private val testScope = TestCoroutineScope(testDispatcher)

    // Use InstantTaskExecutorRule to make LiveData work with JUnit
    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    // Instantiate the ViewModel
    private lateinit var viewModel: WeatherViewModel

    private lateinit var mockResponse: WeatherResponse


    @Before
    fun setup() {

        Dispatchers.setMain(testDispatcher)

        // Initialize the ViewModel with mocks
        viewModel = WeatherViewModel(repository, sharedPreferences, userLocation, context)

        // Define a mock WeatherResponse, dummy values generated
        mockResponse = WeatherResponse(
            base = "base",
            clouds = Clouds(all = 0),
            cod = 200,
            coord = Coord(lat = 0.0, lon = 0.0),
            dt = 1632182400,
            id = 1,
            main = Main(
                feels_like = 20.0,
                humidity = 50,
                pressure = 1013,
                temp = 22.0,
                temp_max = 25.0,
                temp_min = 18.0
            ),
            name = "MockCity",
            sys = Sys(
                country = "US",
                id = 1,
                sunrise = 1632182400,
                sunset = 1632222400,
                type = 1
            ),
            timezone = -14400,
            visibility = 10000,
            weather = listOf(
                Weather(
                    description = "Clear",
                    icon = "01d",
                    id = 800,
                    main = "Clear"
                )
            ),
            wind = Wind(deg = 180, speed = 5.0)
        )
    }

    @Test
    fun `getWeatherData returns success`() {
        // Define LiveData observer to capture changes
        val observer = Observer<Resource<WeatherResponse>> {}

        try {
            // Mock behavior of the repository
            runBlocking {
                `when`(sharedPreferences.getLocation()).thenReturn("New York")
                `when`(repository.getWeather("New York")).thenReturn(Response.success(mockResponse))
            }

            // Observe the LiveData
            viewModel.weatherData.observeForever(observer)

            viewModel.getWeatherData()

            // Verify that the LiveData emits a success state with the expected data
            val result = viewModel.weatherData.getOrAwaitValue()
            assertThat(result.status).isEqualTo(Status.SUCCESS)
        } finally {
            // Remove the observer to avoid leaks
            viewModel.weatherData.removeObserver(observer)
        }
    }

    @Test
    fun `getWeatherData returns error`() {
        // Define an error message
        val errorMessage = "Error: Network Failure"

        // Define a LiveData observer to capture changes
        val observer = Observer<Resource<WeatherResponse>> {}

        try {
            runBlocking {
                // Mock the behavior of the repository to throw an exception
                `when`(sharedPreferences.getLocation()).thenReturn("New York")
                `when`(repository.getWeather("New York")).thenThrow(RuntimeException("Error: Network Failure"))
                //used RuntimeException as IOException requires my method to use this checked exception
            }
            // Observe the LiveData
            viewModel.weatherData.observeForever(observer)

            // Call the method under test
            viewModel.getWeatherData()

            // Verify that the LiveData emits an error state with the expected message
            val result = viewModel.weatherData.getOrAwaitValue()
            assertThat(result).isInstanceOf(Resource::class.java)
            assertThat((result).message).isEqualTo(errorMessage)
        } finally {
            // Remove the observer to avoid leaks
            viewModel.weatherData.removeObserver(observer)
        }
    }

    @Test
    fun `getWeatherByLocation data returns success`() {
        // Define LiveData observer to capture changes
        val observer = Observer<Resource<WeatherResponse>> {}

        try {
            // Mock behavior of the repository
            runBlocking {
                `when`(repository.getWeatherByLocation("100", "-100")).thenReturn(Response.success(mockResponse))
            }

            // Observe the LiveData
            viewModel.weatherUserLocation.observeForever(observer)

            viewModel.getWeatherByLocation("100", "-100")

            // Verify that the LiveData emits a success state with the expected data
            val result = viewModel.weatherUserLocation.getOrAwaitValue()
            assertThat(result).isInstanceOf(Resource::class.java)
            assertThat((result).data).isEqualTo(mockResponse)
        } finally {
            // Remove the observer to avoid leaks
            viewModel.weatherData.removeObserver(observer)
        }
    }

    @Test
    fun `getWeatherByLocation returns network error`() {
        // Define a LiveData observer to capture changes
        val observer = Observer<Resource<WeatherResponse>> {}

        // Mock the behavior of the repository to simulate a network error
        runBlocking {
            `when`(
                repository.getWeatherByLocation("40.7128", "-74.0060"))
                .thenThrow(RuntimeException("Error: Network Failure"))
        //used RuntimeException as IOException requires my method to use this checked exception
        }

        try {
            // Observe the LiveData
            viewModel.weatherUserLocation.observeForever(observer)

            // Call the method under test
            viewModel.getWeatherByLocation("40.7128", "-74.0060")

            // Verify that the LiveData emits a network error state
            val result = viewModel.weatherUserLocation.getOrAwaitValue()
            assertThat(result).isInstanceOf(Resource::class.java)
            assertThat((result as Resource).message).isEqualTo("Error: Network Failure")
        } finally {

            // Remove the observer to avoid leaks
            viewModel.weatherUserLocation.removeObserver(observer)
        }
    }

    @Test
    fun `getLatLonForCity returns coordinates for valid city`() {
        val cityName = "Atlanta"
        val coordinates = viewModel.getLatLonForCity(cityName)
        assertThat(coordinates.first).isEqualTo("33.749")
        assertThat(coordinates.second).isEqualTo("-84.388")
    }

    @Test
    fun `getLatLonForCity returns coordinates for invalid city`() {
        val cityName = "Invalid City"
        val coordinates = viewModel.getLatLonForCity(cityName)
        assertThat(coordinates.first).isNotEqualTo("100")
        assertThat(coordinates.second).isNotEqualTo("-100")
    }

    @After
    fun tearDown() {
        // Clean up resources if necessary - Since I am dealing with Mockito mocks and coroutines in test, no additional cleanup needed.
        // Usually best practice is to perform cleanup to release resources and performed tasks.
        // In my case, MockitoAnnotations in setup takes care of not needing explicit cleanup
    }
}
