package com.example.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ShramRepository(private val dao: ShramDao) {

    val allWorkers: Flow<List<WorkerEntity>> = dao.getAllWorkers()
    val allBookings: Flow<List<BookingEntity>> = dao.getAllBookings()
    val allVotes: Flow<List<CooperativeVoteEntity>> = dao.getAllVotes()

    fun getWorkersByTrade(trade: String): Flow<List<WorkerEntity>> = dao.getWorkersByTrade(trade)

    fun getWorkerById(id: Long): Flow<WorkerEntity?> = dao.getWorkerById(id)

    fun getBookingsForWorker(workerId: Long): Flow<List<BookingEntity>> = dao.getBookingsForWorker(workerId)

    suspend fun createBooking(booking: BookingEntity): Long = dao.insertBooking(booking)

    suspend fun updateBookingStatus(id: Long, status: String) = dao.updateBookingStatus(id, status)

    suspend fun completeBooking(id: Long, rating: Float, review: String, workerId: Long, amount: Int) {
        dao.completeBooking(id, rating, review)
        // 3% allocated to worker's personal dividend accrual pool
        val dividendInc = (amount * 0.03).toInt()
        dao.incrementWorkerStats(workerId, dividendInc)
    }

    suspend fun setWorkerAvailability(id: Long, isAvailable: Boolean) {
        dao.updateWorkerAvailability(id, isAvailable)
    }

    suspend fun castVote(voteId: Long, choice: String, currentYes: Int, currentNo: Int) {
        val newYes = if (choice == "YES") currentYes + 1 else currentYes
        val newNo = if (choice == "NO") currentNo + 1 else currentNo
        dao.recordVote(voteId, choice, newYes, newNo)
    }

    suspend fun checkAndSeedDatabase() {
        dao.insertWorkers(sampleWorkers)
        if (dao.getVoteCount() == 0) {
            dao.insertVotes(sampleVotes)
        }
        // Seed initial demo booking if empty
        val currentBookings = dao.getAllBookings()
        // We will seed an active booking for immediate interactive demo
        dao.insertBooking(
            BookingEntity(
                id = 1,
                customerName = "Ananya Sen",
                customerPhone = "+91 98101 23456",
                customerAddress = "Flat 402, Lotus Heights, Saket, New Delhi",
                workerId = 1,
                workerName = "Rameshwar Sharma",
                trade = "Electrician",
                serviceTitle = "Circuit Breaker Tripping & Switchboard Inspection",
                description = "Master bedroom MCB trips whenever AC turns on. Need urgent diagnosed rewiring.",
                scheduledDate = "Today",
                scheduledTime = "11:30 AM",
                status = "WORKER_ARRIVING",
                totalAmount = 450,
                welfareContribution = 14,
                workerShare = 436,
                otpCode = "4819",
                paymentMethod = "UPI / Jan Dhan",
                isPaid = false
            )
        )
    }

    companion object {
        val sampleWorkers = listOf(
            WorkerEntity(
                id = 1,
                name = "Rameshwar Sharma",
                trade = "Electrician",
                phone = "+91 98712 34501",
                rating = 4.9f,
                reviewCount = 142,
                hourlyRate = 350,
                experienceYears = 14,
                memberId = "SHRAM-DEL-0104",
                sharesOwned = 220,
                dividendEarned = 5480,
                verifiedNsdc = true,
                verifiedEShram = true,
                locality = "Rohini Sector 9, Delhi",
                completedJobs = 389,
                bio = "NSDC Level 4 Certified Master Electrician. 14 years specializing in heavy load balancing, 3-phase wiring, smart switches, and electrical safety audits.",
                specializations = "Short Circuit Repair, MCB Tripping, Inverter Setup, Smart Home Wiring",
                isAvailable = true,
                gender = "Male"
            ),
            WorkerEntity(
                id = 2,
                name = "Kavita Devi",
                trade = "Technician",
                phone = "+91 98114 98210",
                rating = 4.95f,
                reviewCount = 98,
                hourlyRate = 400,
                experienceYears = 7,
                memberId = "SHRAM-DEL-0219",
                sharesOwned = 180,
                dividendEarned = 4320,
                verifiedNsdc = true,
                verifiedEShram = true,
                locality = "Dwarka Sector 12, Delhi",
                completedJobs = 215,
                bio = "NCVET Certified Solar and Heavy Appliance Specialist. Expert in inverter PCB repair, microwave magnetrons, and solar rooftop integration.",
                specializations = "Washing Machine, Microwave, Inverter PCB, Solar Inverter Setup",
                isAvailable = true,
                gender = "Female"
            ),
            WorkerEntity(
                id = 3,
                name = "Mohammad Arif Khan",
                trade = "Carpenter",
                phone = "+91 97180 44221",
                rating = 4.88f,
                reviewCount = 176,
                hourlyRate = 450,
                experienceYears = 18,
                memberId = "SHRAM-DEL-0056",
                sharesOwned = 310,
                dividendEarned = 7890,
                verifiedNsdc = true,
                verifiedEShram = true,
                locality = "Okhla Phase 2, Delhi",
                completedJobs = 512,
                bio = "Master Craftsman (Ustad) & Founding Cooperative Executive. 18 years expertise in teakwood restoration, modular wardrobes, and ergonomic custom fixtures.",
                specializations = "Modular Kitchen, Door Hinges, Hydraulic Bed Repair, Antique Restoration",
                isAvailable = true,
                gender = "Male"
            ),
            WorkerEntity(
                id = 4,
                name = "Sunil Kumar Paswan",
                trade = "Plumber",
                phone = "+91 99580 12398",
                rating = 4.85f,
                reviewCount = 115,
                hourlyRate = 320,
                experienceYears = 9,
                memberId = "SHRAM-DEL-0342",
                sharesOwned = 140,
                dividendEarned = 3650,
                verifiedNsdc = true,
                verifiedEShram = true,
                locality = "Laxmi Nagar, Delhi",
                completedJobs = 278,
                bio = "Certified Plumbing Council artisan. Fast detection of concealed leakage, pressure pump installation, and sanitary bathroom fittings.",
                specializations = "Concealed Pipe Leakage, Jet Spray, RO Fitting, Water Tank Overhaul",
                isAvailable = true,
                gender = "Male"
            ),
            WorkerEntity(
                id = 5,
                name = "Lakshmi Bai",
                trade = "Painter",
                phone = "+91 98188 56712",
                rating = 4.92f,
                reviewCount = 84,
                hourlyRate = 380,
                experienceYears = 8,
                memberId = "SHRAM-DEL-0488",
                sharesOwned = 160,
                dividendEarned = 3980,
                verifiedNsdc = true,
                verifiedEShram = true,
                locality = "Janakpuri, Delhi",
                completedJobs = 164,
                bio = "Wall texture specialist and waterproofing expert. High precision stencil design, non-toxic eco paint application, and damp-proofing.",
                specializations = "Waterproofing, Royale Texture, Crack Filling, Interior Stencil",
                isAvailable = true,
                gender = "Female"
            ),
            WorkerEntity(
                id = 6,
                name = "Joginder Singh",
                trade = "Mason",
                phone = "+91 98733 90123",
                rating = 4.82f,
                reviewCount = 63,
                hourlyRate = 450,
                experienceYears = 16,
                memberId = "SHRAM-DEL-0092",
                sharesOwned = 250,
                dividendEarned = 6200,
                verifiedNsdc = true,
                verifiedEShram = true,
                locality = "Pitampura, Delhi",
                completedJobs = 230,
                bio = "Chief Mason (Raj Mistri) recognized by National Skill Mission. Tile leveling, balcony parapet restoration, and precision marble inlay work.",
                specializations = "Granite Countertops, Vitrified Tile Laying, Plastering, Grouting",
                isAvailable = false,
                gender = "Male"
            ),
            WorkerEntity(
                id = 7,
                name = "Amit Verma",
                trade = "Technician",
                phone = "+91 99102 77812",
                rating = 4.91f,
                reviewCount = 158,
                hourlyRate = 420,
                experienceYears = 10,
                memberId = "SHRAM-DEL-0412",
                sharesOwned = 205,
                dividendEarned = 5120,
                verifiedNsdc = true,
                verifiedEShram = true,
                locality = "Saket, Delhi",
                completedJobs = 340,
                bio = "Certified Inverter AC & HVAC technician. Gas charging (R32/R410A), jet pump deep cleaning, copper pipe flare brazing, and cooling diagnostics.",
                specializations = "Foam Jet Servicing, Gas Leak Repair, PCB Diagnosis, Compressor Replacement",
                isAvailable = true,
                gender = "Male"
            ),
            WorkerEntity(
                id = 8,
                name = "Sunita Mehra",
                trade = "Cleaner",
                phone = "+91 98118 76543",
                rating = 4.93f,
                reviewCount = 189,
                hourlyRate = 299,
                experienceYears = 8,
                memberId = "SHRAM-DEL-0512",
                sharesOwned = 175,
                dividendEarned = 4620,
                verifiedNsdc = true,
                verifiedEShram = true,
                locality = "Lajpat Nagar, Delhi",
                completedJobs = 412,
                bio = "NSDC Certified Deep Cleaning & Sanitization Specialist. Specializes in hospital-grade non-toxic disinfectants, sofa upholstery shampooing, and kitchen grease scrub.",
                specializations = "Deep Kitchen Cleaning, Bathroom Descaling, Sofa Shampooing, Full Home Sanitization",
                isAvailable = true,
                gender = "Female"
            ),
            WorkerEntity(
                id = 9,
                name = "Suman Lata",
                trade = "Caregiver",
                phone = "+91 98711 44321",
                rating = 4.97f,
                reviewCount = 132,
                hourlyRate = 399,
                experienceYears = 11,
                memberId = "SHRAM-DEL-0608",
                sharesOwned = 240,
                dividendEarned = 6180,
                verifiedNsdc = true,
                verifiedEShram = true,
                locality = "Vasant Kunj, Delhi",
                completedJobs = 275,
                bio = "Certified Healthcare Sector Assistant & Red Cross First Aid responder. Compassionate elderly companion care, mobility support, vitals monitoring and medication management.",
                specializations = "Geriatric Care, Post-Op Recovery Support, Blood Pressure & Sugar Vitals, Mobility Assistance",
                isAvailable = true,
                gender = "Female"
            ),
            WorkerEntity(
                id = 10,
                name = "Rajesh Rawat",
                trade = "Driver",
                phone = "+91 98199 87654",
                rating = 4.89f,
                reviewCount = 210,
                hourlyRate = 249,
                experienceYears = 15,
                memberId = "SHRAM-DEL-0724",
                sharesOwned = 290,
                dividendEarned = 7340,
                verifiedNsdc = true,
                verifiedEShram = true,
                locality = "Karol Bagh, Delhi",
                completedJobs = 580,
                bio = "Commercial transport badge holder with certified defensive driving credentials. Zero accident history over 15 years. Expert in luxury automatics, manual SUVs, and outstation trips.",
                specializations = "City Transit, Outstation Highway Drive, Airport Drops, Luxury Car Handling",
                isAvailable = true,
                gender = "Male"
            ),
            WorkerEntity(
                id = 11,
                name = "Ram Prasad Kushwaha",
                trade = "Gardener",
                phone = "+91 99532 11987",
                rating = 4.94f,
                reviewCount = 104,
                hourlyRate = 249,
                experienceYears = 13,
                memberId = "SHRAM-DEL-0831",
                sharesOwned = 190,
                dividendEarned = 4850,
                verifiedNsdc = true,
                verifiedEShram = true,
                locality = "Hauz Khas, Delhi",
                completedJobs = 230,
                bio = "Master Horticulturist & Urban Terrace Garden Specialist. Organic soil revitalization, seasonal flowering setup, vertical garden installations, and neem-based pest control.",
                specializations = "Terrace Garden Setup, Bonsai Shaping, Organic Pest Spray, Lawn Aeration & Mowing",
                isAvailable = true,
                gender = "Male"
            ),
            WorkerEntity(
                id = 12,
                name = "Vikram Malhotra",
                trade = "Technician",
                phone = "+91 97110 55432",
                rating = 4.90f,
                reviewCount = 145,
                hourlyRate = 350,
                experienceYears = 9,
                memberId = "SHRAM-DEL-0945",
                sharesOwned = 210,
                dividendEarned = 5300,
                verifiedNsdc = true,
                verifiedEShram = true,
                locality = "Noida Sector 62, Delhi NCR",
                completedJobs = 310,
                bio = "NSDC Level 4 Certified Consumer Electronics and Multi-Appliance Technician. Diagnostic repair of microwave ovens, washing machine direct-drive motors, and RO purifiers.",
                specializations = "RO Water Purifier, Microwave Magnetron, Chimney Servicing, Front Load Washer",
                isAvailable = true,
                gender = "Male"
            )
        )

        val sampleVotes = listOf(
            CooperativeVoteEntity(
                id = 1,
                resolutionNumber = "RES-2026-08",
                title = "Cooperative Tool Insurance & Subsidy Pool",
                description = "Allocate 1.5% of annual cooperative surplus towards zero-cost tool replacement insurance (accidental drop/theft of drills, gauges, pipe benders).",
                category = "TOOL_SUBSIDY",
                yesVotes = 184,
                noVotes = 12,
                userVoted = null,
                quorumReached = true,
                closesInDays = 2
            ),
            CooperativeVoteEntity(
                id = 2,
                resolutionNumber = "RES-2026-09",
                title = "Standard Wage Floor Revision for Electricians",
                description = "Increase minimum standardized cooperative hourly wage floor from ₹300/hr to ₹350/hr across Delhi NCR chapters to match inflation.",
                category = "WAGE_POLICY",
                yesVotes = 230,
                noVotes = 18,
                userVoted = null,
                quorumReached = true,
                closesInDays = 5
            ),
            CooperativeVoteEntity(
                id = 3,
                resolutionNumber = "RES-2026-10",
                title = "Mahila Shramik Technical Skill Fellowship",
                description = "Sponsor 50 women artisans for NCVET Solar Technician & EV Charging Station certifications with full stipend during training.",
                category = "MEMBERSHIP",
                yesVotes = 295,
                noVotes = 6,
                userVoted = null,
                quorumReached = true,
                closesInDays = 8
            )
        )
    }
}
