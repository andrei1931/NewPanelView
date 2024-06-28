import com.example.pv_menu.ApiService
import com.example.pv_menu.DashboardRepository
import com.example.pv_menu.GenerationPower
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class DashboardRepositoryTest {

    @Mock
    private lateinit var mockApiService: ApiService

    private lateinit var dashboardRepository:
            DashboardRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        dashboardRepository = DashboardRepository(mockApiService)
    }

    @Test
    fun `fetchData should return list of GenerationPower on success`() = runBlocking {
        // Mock data and parameters
        val systemId = "10"
        val start = "2024-01-01"
        val end = "2024-01-02"
        val expectedData = listOf(
            GenerationPower(0.0),
            GenerationPower(5.0),
            GenerationPower(10.0)
        )

        // Mock ApiService response
        `when`(mockApiService.getPowerData(systemId, start, end)).thenReturn(expectedData)

        // Call the method under test
        val result = dashboardRepository.fetchData(systemId, start, end)

        // Assert the result
        assertEquals(expectedData, result)
    }
}
