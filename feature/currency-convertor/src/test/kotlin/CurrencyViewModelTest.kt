import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.loc.currencyconvertor.CurrencyConvertorViewModel
import com.mc.testing.repository.TestCurrencyRepo
import com.mc.testing.worker.TestWorkManagerSyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CurrencyViewModelTest {

    lateinit var viewModel: CurrencyConvertorViewModel
    private lateinit var currencyRepo: TestCurrencyRepo

    @Mock
    lateinit var sharedPreferences: SharedPreferences

    private val workManagerSyncManager = TestWorkManagerSyncManager()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        currencyRepo = TestCurrencyRepo()
        MockitoAnnotations.openMocks(this)

        // Changing the dispatcher because the default dispatcher for ViewModel is Android Main Dispatcher and we don't have that in local tests.
        Dispatchers.setMain(testDispatcher)

        viewModel = CurrencyConvertorViewModel(
            currencyRepository = currencyRepo,
            sharedPreferences = sharedPreferences,
            workManagerSyncManager = workManagerSyncManager,
        )
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() {
        assert(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `state is not loading when syncing is not loading`() = testScope.runTest {
        workManagerSyncManager.emit(false)
        val job = launch {
            viewModel.uiState.take(1).toList()
        }
        job.join()
        assert(viewModel.uiState.value.isLoading.not())
    }

}