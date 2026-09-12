package com.example

import com.example.data.BookingTrackingStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun bookingTrackingStatus_sequenceMatchesRequiredWorkflow() {
        assertEquals(BookingTrackingStatus.CONFIRMED, BookingTrackingStatus.from("CONFIRMED"))
        assertEquals(BookingTrackingStatus.WORKER_ASSIGNED, BookingTrackingStatus.from("WORKER_ASSIGNED"))
        assertEquals(BookingTrackingStatus.WORKER_ARRIVING, BookingTrackingStatus.from("WORKER_ARRIVING"))
        assertEquals(BookingTrackingStatus.SERVICE_STARTED, BookingTrackingStatus.from("SERVICE_STARTED"))
        assertEquals(BookingTrackingStatus.COMPLETED, BookingTrackingStatus.from("COMPLETED"))

        // Backward compatibility mappings
        assertEquals(BookingTrackingStatus.CONFIRMED, BookingTrackingStatus.from("REQUESTED"))
        assertEquals(BookingTrackingStatus.WORKER_ASSIGNED, BookingTrackingStatus.from("ACCEPTED"))
        assertEquals(BookingTrackingStatus.WORKER_ARRIVING, BookingTrackingStatus.from("EN_ROUTE"))
        assertEquals(BookingTrackingStatus.SERVICE_STARTED, BookingTrackingStatus.from("IN_PROGRESS"))
    }

    @Test
    fun bookingTrackingStatus_progressionTransitions() {
        assertEquals(BookingTrackingStatus.WORKER_ASSIGNED, BookingTrackingStatus.CONFIRMED.nextStatus())
        assertEquals(BookingTrackingStatus.WORKER_ARRIVING, BookingTrackingStatus.WORKER_ASSIGNED.nextStatus())
        assertEquals(BookingTrackingStatus.SERVICE_STARTED, BookingTrackingStatus.WORKER_ARRIVING.nextStatus())
        assertEquals(BookingTrackingStatus.COMPLETED, BookingTrackingStatus.SERVICE_STARTED.nextStatus())
        assertNull(BookingTrackingStatus.COMPLETED.nextStatus())
    }

    @Test
    fun bookingTrackingStatus_allHaveLabelsAndDescriptions() {
        BookingTrackingStatus.entries.forEach { status ->
            assertNotNull(status.title)
            assertNotNull(status.description)
            assertNotNull(status.code)
        }
    }

    @Test
    fun mockLocationService_providesAccurateNearbyWorkers() {
        val loc1 = com.example.ui.components.MockLocationService.workerLocations[1L]
        assertNotNull(loc1)
        assertEquals(0.8, loc1!!.distanceKm, 0.001)
        assertEquals(true, loc1.isAvailable)
        assertEquals("Mayur Vihar Ext.", loc1.localityLabel)

        // Verify unavailable worker status
        val loc10 = com.example.ui.components.MockLocationService.workerLocations[10L]
        assertNotNull(loc10)
        assertEquals(false, loc10!!.isAvailable)
    }
}

