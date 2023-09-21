package info.nightscout.androidaps.plugins.pump.medtronic.comm

import android.util.Log
import info.nightscout.androidaps.plugins.pump.medtronic.MedtronicTestBase
import info.nightscout.androidaps.plugins.pump.medtronic.comm.history.RawHistoryPage
import info.nightscout.androidaps.plugins.pump.medtronic.comm.history.pump.MedtronicPumpHistoryDecoder
import info.nightscout.androidaps.plugins.pump.medtronic.comm.history.pump.PumpHistoryEntry
import info.nightscout.androidaps.plugins.pump.medtronic.comm.history.pump.PumpHistoryEntryType
import info.nightscout.androidaps.plugins.pump.medtronic.data.MedtronicHistoryData
import info.nightscout.androidaps.plugins.pump.medtronic.data.dto.TempBasalProcessDTO
import info.nightscout.androidaps.plugins.pump.medtronic.defs.MedtronicDeviceType
import info.nightscout.androidaps.plugins.pump.medtronic.driver.MedtronicPumpStatus
import info.nightscout.androidaps.plugins.pump.medtronic.util.MedtronicUtil
import info.nightscout.database.impl.serialisation.SealedClassHelper
import info.nightscout.interfaces.ui.UiInteraction
import info.nightscout.pump.common.utils.ByteUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock

/**
 * Created by andy on 3/10/19.
 */
@Suppress("unused") class MedtronicHistoryDataUTest : MedtronicTestBase() {

    @Mock lateinit var uiInteraction: UiInteraction
    //TestLogger LOGGER = TestLoggerFactory.getTestLogger(MedtronicHistoryDataUTest.class);
    // var historyPageData = ByteUtil
    //     .createByteArrayFromString(
    //         "16 00 12 EC 14 47 13 33 00 14 F2 14 47 13 00 16 01 14 F2 14 47 13 33 00 1C C9 15 47 13 00 16 00 1C C9 15 47 13 33 4E 31 D3 15 47 13 00 16 01 31 D3 15 47 13 33 00 1A F1 15 47 13 00 16 00 1A F1 15 47 13 33 50 1D F1 15 47 13 00 16 01 1D F1 15 47 13 33 50 11 D8 16 47 13 00 16 01 11 D8 16 47 13 33 50 31 FB 16 47 13 00 16 01 31 FB 16 47 13 33 50 12 E3 17 47 13 00 16 01 12 E3 17 47 13 33 00 1E FB 17 47 13 00 16 00 1E FB 17 47 13 33 D8 21 FB 17 47 13 00 16 01 21 FB 17 47 13 07 00 00 05 CC 27 93 6D 27 93 05 0C 00 E8 00 00 00 00 05 CC 05 CC 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0C 00 E8 00 00 00 33 00 36 C4 00 48 13 00 16 00 36 C4 00 48 13 33 D8 29 C9 00 48 13 00 16 01 29 C9 00 48 13 33 00 12 E7 00 48 13 00 16 00 12 E7 00 48 13 33 BC 19 C9 01 48 13 00 16 01 19 C9 01 48 13 33 00 26 CE 01 48 13 00 16 00 26 CE 01 48 13 33 44 29 CE 01 48 13 00 16 01 29 CE 01 48 13 33 00 13 D3 01 48 13 00 16 00 13 D3 01 48 13 33 64 31 F1 01 48 13 00 16 01 31 F1 01 48 13 33 00 0B F7 01 48 13 00 16 00 0B F7 01 48 13 33 00 12 D8 02 48 13 00 16 01 12 D8 02 48 13 33 00 10 F1 02 48 13 00 16 00 10 F1 02 48 13 33 00 30 C4 03 48 13 00 16 01 30 C4 03 48 13 33 00 04 CA 03 48 13 00 16 00 04 CA 03 48 13 33 00 2F D3 03 48 13 00 16 01 2F D3 03 48 13 33 00 30 D8 03 48 13 00 16 00 30 D8 03 48 13 33 00 13 E7 03 48 13 00 16 01 13 E7 03 48 13 33 00 2E FB 03 48 13 00 16 00 2E FB 03 48 13 19 00 00 C1 04 08 13 07 00 00 04 0C 28 93 6D 28 93 05 0C 00 E8 00 00 00 00 04 0C 04 0C 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0C 00 E8 00 00 00 06 3E 03 7A 19 DC 48 49 13 0C 3E 0C E6 08 09 13 07 00 00 01 E4 29 93 6D 29 93 05 0C 00 E8 00 00 00 00 01 E4 01 E4 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0C 00 E8 00 00 00 1A 00 13 D2 0D 0A 13 1A 01 28 D2 0D 0A 13 21 00 2A D8 0D 0A 13 03 00 00 00 0E 2D D9 2D 0A 13 33 98 26 DE 0D 4A 13 00 16 01 26 DE 0D 4A 13 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 5D 70"
    //     )

    private lateinit var medtronicHistoryData: MedtronicHistoryData
    private lateinit var medtronicPumpStatus: MedtronicPumpStatus

    @BeforeEach
    fun setup() {
        medtronicPumpStatus = MedtronicPumpStatus(rh, sp, rxBus, rileyLinkUtil)
        medtronicUtil = MedtronicUtil(aapsLogger, rxBus, rileyLinkUtil, medtronicPumpStatus, uiInteraction)
        decoder = MedtronicPumpHistoryDecoder(aapsLogger, medtronicUtil)
        medtronicHistoryData = MedtronicHistoryData(
            packetInjector, aapsLogger, sp, rh, rxBus, activePlugin,
            medtronicUtil, decoder, medtronicPumpStatus, pumpSync, pumpSyncStorage, uiInteraction
        )


        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")

    }

    //@Test
    @Throws(Exception::class) fun testTBR() {
        val historyPageData = ByteUtil
            .createByteArrayFromString(
                "16 00 12 EC 14 47 13 33 00 14 F2 14 47 13 00 16 01 14 F2 14 47 13 33 00 1C C9 15 47 13 00 16 00 1C C9 15 47 13 33 4E 31 D3 15 47 13 00 16 01 31 D3 15 47 13 33 00 1A F1 15 47 13 00 16 00 1A F1 15 47 13 33 50 1D F1 15 47 13 00 16 01 1D F1 15 47 13 33 50 11 D8 16 47 13 00 16 01 11 D8 16 47 13 33 50 31 FB 16 47 13 00 16 01 31 FB 16 47 13 33 50 12 E3 17 47 13 00 16 01 12 E3 17 47 13 33 00 1E FB 17 47 13 00 16 00 1E FB 17 47 13 33 D8 21 FB 17 47 13 00 16 01 21 FB 17 47 13 07 00 00 05 CC 27 93 6D 27 93 05 0C 00 E8 00 00 00 00 05 CC 05 CC 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0C 00 E8 00 00 00 33 00 36 C4 00 48 13 00 16 00 36 C4 00 48 13 33 D8 29 C9 00 48 13 00 16 01 29 C9 00 48 13 33 00 12 E7 00 48 13 00 16 00 12 E7 00 48 13 33 BC 19 C9 01 48 13 00 16 01 19 C9 01 48 13 33 00 26 CE 01 48 13 00 16 00 26 CE 01 48 13 33 44 29 CE 01 48 13 00 16 01 29 CE 01 48 13 33 00 13 D3 01 48 13 00 16 00 13 D3 01 48 13 33 64 31 F1 01 48 13 00 16 01 31 F1 01 48 13 33 00 0B F7 01 48 13 00 16 00 0B F7 01 48 13 33 00 12 D8 02 48 13 00 16 01 12 D8 02 48 13 33 00 10 F1 02 48 13 00 16 00 10 F1 02 48 13 33 00 30 C4 03 48 13 00 16 01 30 C4 03 48 13 33 00 04 CA 03 48 13 00 16 00 04 CA 03 48 13 33 00 2F D3 03 48 13 00 16 01 2F D3 03 48 13 33 00 30 D8 03 48 13 00 16 00 30 D8 03 48 13 33 00 13 E7 03 48 13 00 16 01 13 E7 03 48 13 33 00 2E FB 03 48 13 00 16 00 2E FB 03 48 13 19 00 00 C1 04 08 13 07 00 00 04 0C 28 93 6D 28 93 05 0C 00 E8 00 00 00 00 04 0C 04 0C 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0C 00 E8 00 00 00 06 3E 03 7A 19 DC 48 49 13 0C 3E 0C E6 08 09 13 07 00 00 01 E4 29 93 6D 29 93 05 0C 00 E8 00 00 00 00 01 E4 01 E4 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0C 00 E8 00 00 00 1A 00 13 D2 0D 0A 13 1A 01 28 D2 0D 0A 13 21 00 2A D8 0D 0A 13 03 00 00 00 0E 2D D9 2D 0A 13 33 98 26 DE 0D 4A 13 00 16 01 26 DE 0D 4A 13 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 5D 70"
            )

        val historyPage = RawHistoryPage(aapsLogger)
        historyPage.appendData(historyPageData)
        val pumpHistoryEntries: List<PumpHistoryEntry> = decoder.processPageAndCreateRecords(historyPage)
        println("PumpHistoryEntries: " + pumpHistoryEntries.size)
        Log.d("Test", "Log.d")
        //LOGGER.debug("Logger.debug");
        for (pumpHistoryEntry in pumpHistoryEntries) {
            Log.d("MedtronicHistoryDataUTest", pumpHistoryEntry.toString())
        }
    }

    fun testRussel() {
        val historyPageData = ByteUtil
            .createByteArrayFromString(
                "06 15 04 F6 00 40 60 01 05 06 36 04 FE 00 40 60 01 05 06 2F 18 1A 00 40 20 C1 05 06 2F 0C 45 00 40 20 C1 05 06 2F 0C 56 00 40 20 C1 05 06 2F 0C 78 00 40 20 C1 05 06 2F 0C AD 00 40 20 C1 05 06 15 04 BA 00 40 40 A1 05 0C 15 0E 40 00 01 05 64 00 0D 44 00 01 05 17 00 14 44 00 01 05 18 00 00 44 00 01 05 21 00 07 44 00 01 05 21 00 0C 4E 00 01 05 07 00 00 00 00 01 85 6D 01 85 06 08 00 2B 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 08 00 2B 00 00 00 03 00 00 00 15 00 67 35 02 05 03 00 03 00 03 1C 67 15 02 05 07 00 00 00 40 02 85 6D 02 85 06 08 00 2B 00 00 00 00 00 40 00 40 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 08 00 2B 00 00 00 2C 78 39 5F 17 03 05 07 00 00 02 6C 03 85 6D 03 85 06 08 00 2B 00 00 00 00 02 6C 02 6C 64 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 08 00 2B 00 00 00 26 01 33 44 01 04 05 27 03 74 41 01 B2 07 28 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 29 74"
            )
        val historyPage = RawHistoryPage(aapsLogger)
        historyPage.appendData(historyPageData)
        val pumpHistoryEntries: List<PumpHistoryEntry> = decoder.processPageAndCreateRecords(historyPage)
        println("PumpHistoryEntries: " + pumpHistoryEntries.size)
        Log.d("Test", "Log.d")
        //LOGGER.debug("Logger.debug");
        for (pumpHistoryEntry in pumpHistoryEntries) {
            Log.d("MedtronicHistoryDataUTest", pumpHistoryEntry.toString())
        }
    }

    // @Test
    @Throws(Exception::class) fun testJRoth_2111() {
        val historyPageData = ByteUtil
            .createByteArrayFromString(
                "01 03 03 00 8E 85 52 48 13 33 00 AB 89 12 48 13 00 16 00 AB 89 12 48 13 33 34 AD 89 12 48 13 00 16 01 AD 89 12 48 13 01 01 01 00 B8 8A 52 48 13 01 08 08 00 9F 8C 52 48 13 33 00 91 8F 12 48 13 00 16 00 91 8F 12 48 13 33 00 92 8F 12 48 13 00 16 03 92 8F 12 48 13 33 00 BA A7 12 48 13 00 16 04 BA A7 12 48 13 01 19 19 00 AF B0 52 48 13 33 00 8C 8A 13 48 13 00 16 04 8C 8A 13 48 13 33 00 9D A8 13 48 13 00 16 04 9D A8 13 48 13 33 00 AA 85 14 48 13 00 16 04 AA 85 14 48 13 33 00 8D A1 14 48 13 00 16 04 8D A1 14 48 13 33 10 89 BA 14 48 13 00 16 01 89 BA 14 48 13 33 00 AD 88 15 48 13 00 16 00 AD 88 15 48 13 33 00 AF 88 15 48 13 00 16 01 AF 88 15 48 13 01 1D 1D 00 95 8D 55 48 13 33 00 95 92 15 48 13 00 16 04 95 92 15 48 13 33 1E B7 9C 15 48 13 00 16 01 B7 9C 15 48 13 33 00 AA A6 15 48 13 00 16 00 AA A6 15 48 13 33 00 AC A6 15 48 13 00 16 04 AC A6 15 48 13 01 02 02 00 B7 A6 55 48 13 01 01 01 00 A6 AC 55 48 13 33 00 B3 8D 16 48 13 00 16 04 B3 8D 16 48 13 33 00 B7 97 16 48 13 00 16 04 B7 97 16 48 13 33 18 A7 B2 16 48 13 00 16 01 A7 B2 16 48 13 33 00 8B B8 16 48 13 00 16 00 8B B8 16 48 13 33 00 8D B8 16 48 13 00 16 03 8D B8 16 48 13 33 18 AE 85 17 48 13 00 16 01 AE 85 17 48 13 33 00 92 8A 17 48 13 00 16 00 92 8A 17 48 13 33 00 94 8A 17 48 13 00 16 01 94 8A 17 48 13 01 02 02 00 9F 8A 57 48 13 33 06 AC 8F 17 48 13 00 16 01 AC 8F 17 48 13 01 02 02 00 B8 8F 57 48 13 33 00 98 94 17 48 13 00 16 00 98 94 17 48 13 33 0C 9A 94 17 48 13 00 16 01 9A 94 17 48 13 01 02 02 00 A5 94 57 48 13 33 00 9C 99 17 48 13 00 16 00 9C 99 17 48 13 33 00 9E 99 17 48 13 00 16 01 9E 99 17 48 13 01 02 02 00 A9 99 57 48 13 01 02 02 00 84 9F 57 48 13 01 02 02 00 A7 A6 57 48 13 33 00 A4 AB 17 48 13 00 16 00 A4 AB 17 48 13 01 02 02 00 B0 AB 57 48 13 33 00 A7 B0 17 48 13 00 16 02 A7 B0 17 48 13 01 01 01 00 B2 B0 57 48 13 33 00 AD BA 17 48 13 00 16 04 AD BA 17 48 13 07 00 00 05 3A A8 13 6D A8 13 05 0C 00 E8 00 00 00 00 05 3A 00 F6 12 04 44 52 00 00 04 44 52 00 00 00 00 00 00 04 44 64 35 00 00 00 35 0C 00 E8 00 00 00 06 0A 1D 66 80 81 60 09 13 0C 0A 8D 82 00 09 13 1A 00 9A 82 00 09 13 1A 01 AF 82 00 09 13 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 05 28"
            )
        val historyPage = RawHistoryPage(aapsLogger)
        historyPage.appendData(historyPageData)
        val pumpHistoryEntries: List<PumpHistoryEntry> = decoder.processPageAndCreateRecords(historyPage)
        println("PumpHistoryEntries: " + pumpHistoryEntries.size)
        Log.d("Test", "Log.d")
        //LOGGER.debug("Logger.debug");
        for (pumpHistoryEntry in pumpHistoryEntries) {
            Log.d("MedtronicHistoryDataUTest", pumpHistoryEntry.toString())
        }
    }

    @Test @Throws(Exception::class) fun test_1724() {
        val historyPageData = ByteUtil
            .createByteArrayFromString(
                "33 20 53 78 15 51 16 00 16 01 53 78 15 51 16 33 00 6F 40 16 51 16 00 16 00 6F 40 16 51 16 7B 16 6F 40 16 11 16 2C 1E 00 33 30 71 40 16 51 16 00 16 01 71 40 16 51 16 33 00 6E 45 16 51 16 00 16 00 6E 45 16 51 16 7B 16 6E 45 16 11 16 2C 1E 00 33 3A 70 45 16 51 16 00 16 01 70 45 16 51 16 33 00 71 5E 16 51 16 00 16 00 71 5E 16 51 16 7B 16 71 5E 16 11 16 2C 1E 00 33 40 73 5E 16 51 16 00 16 01 73 5E 16 51 16 33 00 74 6D 16 51 16 00 16 00 74 6D 16 51 16 7B 16 74 6D 16 11 16 2C 1E 00 33 14 76 6D 16 51 16 00 16 01 76 6D 16 51 16 33 00 77 72 16 51 16 00 16 00 77 72 16 51 16 7B 16 77 72 16 11 16 2C 1E 00 7B 17 40 40 17 11 16 2E 1E 00 33 28 51 41 17 51 16 00 16 01 51 41 17 51 16 33 00 56 46 17 51 16 00 16 00 56 46 17 51 16 7B 17 56 46 17 11 16 2E 1E 00 33 34 59 46 17 51 16 00 16 01 59 46 17 51 16 33 00 70 4A 17 51 16 00 16 00 70 4A 17 51 16 7B 17 70 4A 17 11 16 2E 1E 00 33 58 72 4A 17 51 16 00 16 01 72 4A 17 51 16 33 00 6E 59 17 51 16 00 16 00 6E 59 17 51 16 7B 17 6E 59 17 11 16 2E 1E 00 33 18 70 59 17 51 16 00 16 01 70 59 17 51 16 33 00 70 5E 17 51 16 00 16 00 70 5E 17 51 16 7B 17 70 5E 17 11 16 2E 1E 00 33 0C 72 5E 17 51 16 00 16 01 72 5E 17 51 16 33 00 72 63 17 51 16 00 16 00 72 63 17 51 16 7B 17 72 63 17 11 16 2E 1E 00 33 1C 70 72 17 51 16 00 16 01 70 72 17 51 16 33 00 51 78 17 51 16 00 16 00 51 78 17 51 16 7B 17 52 78 17 11 16 2E 1E 00 33 12 54 78 17 51 16 00 16 01 54 78 17 51 16 07 00 00 04 4E 51 96 00 00 00 6E 51 96 05 00 00 00 00 00 00 00 04 4E 03 A2 54 00 AC 10 00 00 00 00 00 00 00 00 00 AC 00 00 00 24 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 33 00 46 46 00 52 16 00 16 00 46 46 00 52 16 7B 00 47 46 00 12 16 00 1E 00 33 20 47 64 00 52 16 00 16 01 47 64 00 52 16 33 00 6D 77 00 52 16 00 16 00 6D 77 00 52 16 7B 00 6D 77 00 12 16 00 1E 00 33 12 70 77 00 52 16 00 16 01 70 77 00 52 16 33 00 58 78 00 52 16 00 16 00 58 78 00 52 16 7B 00 59 78 00 12 16 00 1E 00 33 00 5C 78 00 52 16 00 16 02 5C 78 00 52 16 21 00 66 79 00 12 16 03 00 00 00 9C 74 42 21 12 16 03 00 03 00 03 6E 4D 01 12 16 33 00 79 4E 01 52 16 00 16 00 79 4E 01 52 16 7B 01 79 4E 01 12 16 02 1E 00 33 2A 6B 4F 01 52 16 00 16 01 6B 4F 01 52 16 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 2E D1"
                //"33 20 53 78 15 51 16 00 16 01 53 78 15 51 16 33 00 6F 40 16 51 16 00 16 00 6F 40 16 51 16 7B 16 6F 40 16 11 16 2C 1E 00 33 30 71 40 16 51 16 00 16 01 71 40 16 51 16 33 00 6E 45
                // 16 51 16 00 16 00 6E 45 16 51 16 7B 16 6E 45 16 11 16 2C 1E 00 33 3A 70 45 16 51 16 00 16 01 70 45 16 51 16 33 00 71 5E 16 51 16 00 16 00 71 5E 16 51 16 7B 16 71 5E 16 11 16 2C 1E 00 33 40 73 5E 16 51 16 00 16 01 73 5E 16 51 16 33 00 74 6D 16 51 16 00 16 00 74 6D 16 51 16 7B 16 74 6D 16 11 16 2C 1E 00 33 14 76 6D 16 51 16 00 16 01 76 6D 16 51 16 33 00 77 72 16 51 16 00 16 00 77 72 16 51 16 7B 16 77 72 16 11 16 2C 1E 00 7B 17 40 40 17 11 16 2E 1E 00 33 28 51 41 17 51 16 00 16 01 51 41 17 51 16 33 00 56 46 17 51 16 00 16 00 56 46 17 51 16 7B 17 56 46 17 11 16 2E 1E 00 33 34 59 46 17 51 16 00 16 01 59 46 17 51 16 33 00 70 4A 17 51 16 00 16 00 70 4A 17 51 16 7B 17 70 4A 17 11 16 2E 1E 00 33 58 72 4A 17 51 16 00 16 01 72 4A 17 51 16 33 00 6E 59 17 51 16 00 16 00 6E 59 17 51 16 7B 17 6E 59 17 11 16 2E 1E 00 33 18 70 59 17 51 16 00 16 01 70 59 17 51 16 33 00 70 5E 17 51 16 00 16 00 70 5E 17 51 16 7B 17 70 5E 17 11 16 2E 1E 00 33 0C 72 5E 17 51 16 00 16 01 72 5E 17 51 16 33 00 72 63 17 51 16 00 16 00 72 63 17 51 16 7B 17 72 63 17 11 16 2E 1E 00 33 1C 70 72 17 51 16 00 16 01 70 72 17 51 16 33 00 51 78 17 51 16 00 16 00 51 78 17 51 16 7B 17 52 78 17 11 16 2E 1E 00 33 12 54 78 17 51 16 00 16 01 54 78 17 51 16 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 4D 6D"
            )

        medtronicUtil.medtronicPumpModel = MedtronicDeviceType.Medtronic_723_Revel
        medtronicUtil.isModelSet = true

        val historyPage = RawHistoryPage(aapsLogger)
        historyPage.appendData(historyPageData)
        val pumpHistoryEntries: MutableList<PumpHistoryEntry> = decoder.processPageAndCreateRecords(historyPage)
        println("PumpHistoryEntries: " + pumpHistoryEntries.size)

        val rewindRecords: MutableList<PumpHistoryEntry> = medtronicHistoryData.getFilteredItems(pumpHistoryEntries, PumpHistoryEntryType.Rewind)

        preProcessListTBR(pumpHistoryEntries)

        println("PumpHistoryEntries: after: " + pumpHistoryEntries.size)

        Log.d("Test", "Log.d")
        //LOGGER.debug("Logger.debug");
        for (pumpHistoryEntry in pumpHistoryEntries) {
            println(pumpHistoryEntry.toEntryString())
        }

        println("PumpHistoryEntries: after: " + pumpHistoryEntries.size)

        val tbrs: MutableList<PumpHistoryEntry> = medtronicHistoryData.getFilteredItems(pumpHistoryEntries, PumpHistoryEntryType.TempBasalCombined)

        tbrs.reverse()

        println("PumpHistoryEntries: getFilteredItems: " + tbrs.size)

        println("PumpHistoryEntries: getRewindItems: $rewindRecords.size : " + SealedClassHelper.gson.toJson(rewindRecords))

        val processList: MutableList<TempBasalProcessDTO> = medtronicHistoryData.createTBRProcessList(tbrs)

        println("PumpHistoryEntries: processList: " + processList.size)

        for (tempBasalProcessDTO in processList) {
            println(tempBasalProcessDTO.toTreatmentString())
        }

    }
}