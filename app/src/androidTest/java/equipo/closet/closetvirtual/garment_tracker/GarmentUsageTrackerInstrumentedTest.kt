package equipo.closet.closetvirtual.garment_tracker


import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import equipo.closet.closetvirtual.entities.Garment
import equipo.closet.closetvirtual.repositories.FirebaseGarmentRepository
import equipo.closet.closetvirtual.repositories.GarmentUsageTracker
import equipo.closet.closetvirtual.repositories.interfaces.IGarmentUsageTracker
import equipo.closet.closetvirtual.repositories.interfaces.Repository
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class GarmentUsageTrackerInstrumentedTest {

    private lateinit var db: FirebaseFirestore
    private lateinit var tracker: IGarmentUsageTracker

    @Before
    fun setup() {
        db = FirebaseFirestore.getInstance()

        tracker = GarmentUsageTracker
    }

    @Test(timeout = 10_000)
    fun countGarmentUsages_shouldReturnCorrectCount() {
        runBlocking {
            // Arrange
            val garmentId = "test-garment-${UUID.randomUUID()}"
            val id1 = tracker.registerUsage(garmentId)
            val id2 = tracker.registerUsage(garmentId)

            // Act
            val count = tracker.countGarmentUsages(garmentId)

            // Assert
            assertTrue(count >= 2)

            // Cleanup
            db.collection("garment_usage").document(id1).delete().await()
            db.collection("garment_usage").document(id2).delete().await()
        }
    }

    @Test(timeout = 10_000)
    fun registerUsage_shouldCreateUsageDocument() {
        runBlocking {
            // Arrange
            val garmentId = "test-garment-${UUID.randomUUID()}"

            // Act
            val usageId = tracker.registerUsage(garmentId)

            // Assert
            val snapshot = db.collection("garment_usage").document(usageId).get().await()
            assertTrue(snapshot.exists())
            assertEquals(garmentId, snapshot.getString("garmentId"))

            // Cleanup
            db.collection("garment_usage").document(usageId).delete().await()
        }
    }

    @Test(timeout = 10_000)
    fun countGarmentUsageMonthly_shouldReturnCorrectCount() {
        runBlocking {
            // Arrange
            val garmentId = "test-garment-${UUID.randomUUID()}"
            val id1 = tracker.registerUsage(garmentId)
            val id2 = tracker.registerUsage(garmentId)

            // Act
            val count = tracker.countGarmentUsageMonthly(garmentId)

            // Assert
            assertTrue(count >= 2)

            // Cleanup
            db.collection("garment_usage").document(id1).delete().await()
            db.collection("garment_usage").document(id2).delete().await()
        }
    }

    @Test(timeout = 10_000)
    fun deleteUsage_shouldRemoveAllGarmentUsages() {
        runBlocking {
            // Arrange
            val garmentId = "test-garment-${UUID.randomUUID()}"
            val id1 = tracker.registerUsage(garmentId)
            val id2 = tracker.registerUsage(garmentId)

            // Act
            tracker.deleteUsage(garmentId)

            // Assert
            val snapshot = db.collection("garment_usage")
                .whereEqualTo("garmentId", garmentId)
                .get()
                .await()

            assertEquals(0, snapshot.documents.size)
        }
    }
}