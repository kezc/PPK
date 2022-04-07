package com.put.ubi.data

import com.put.ubi.model.Fund

class FundsProvider {
    fun getFunds(): List<Fund> {
        return listOf(
            Fund(
                name = "PKO Emerytura 2025",
                bankierURL = "https://www.bankier.pl/fundusze/notowania/PCS74",
                thumbnail = "https://www.mojeppk.pl/dam/jcr:4d6c353d-40a1-46d7-a2b9-c5332af85aa3/PKOTFI_logotyp.jpg"
            ),
            Fund(
                name = "PKO Emerytura 2030",
                bankierURL = "https://www.bankier.pl/fundusze/notowania/PCS75",
                thumbnail = "https://www.mojeppk.pl/dam/jcr:4d6c353d-40a1-46d7-a2b9-c5332af85aa3/PKOTFI_logotyp.jpg"
            ),
            Fund(
                name = "PKO Emerytura 2035",
                bankierURL = "https://www.bankier.pl/fundusze/notowania/PCS76",
                thumbnail = "https://www.mojeppk.pl/dam/jcr:4d6c353d-40a1-46d7-a2b9-c5332af85aa3/PKOTFI_logotyp.jpg"
            ),
            Fund(
                name = "PKO Emerytura 2040",
                bankierURL = "https://www.bankier.pl/fundusze/notowania/PCS77",
                thumbnail = "https://www.mojeppk.pl/dam/jcr:4d6c353d-40a1-46d7-a2b9-c5332af85aa3/PKOTFI_logotyp.jpg"
            ),
            Fund(
                name = "Allianz Plan Emerytalny 2025",
                bankierURL = "https://www.bankier.pl/fundusze/notowania/ALL82",
                thumbnail = "https://www.mojeppk.pl/dam/jcr:891d8e47-423b-425e-8592-6444b7ae8b1e/Allianz_TFI_logotyp_v3.png"
            ),
            Fund(
                name = "Allianz Plan Emerytalny 2030",
                bankierURL = "https://www.bankier.pl/fundusze/notowania/ALL83",
                thumbnail = "https://www.mojeppk.pl/dam/jcr:891d8e47-423b-425e-8592-6444b7ae8b1e/Allianz_TFI_logotyp_v3.png"
            ),
            Fund(
                name = "Allianz Plan Emerytalny 2035",
                bankierURL = "https://www.bankier.pl/fundusze/notowania/ALL84",
                thumbnail = "https://www.mojeppk.pl/dam/jcr:891d8e47-423b-425e-8592-6444b7ae8b1e/Allianz_TFI_logotyp_v3.png"
            ),
            Fund(
                name = "Allianz Plan Emerytalny 2040",
                bankierURL = "https://www.bankier.pl/fundusze/notowania/ALL85",
                thumbnail = "https://www.mojeppk.pl/dam/jcr:891d8e47-423b-425e-8592-6444b7ae8b1e/Allianz_TFI_logotyp_v3.png"
            ),

        )
    }
}