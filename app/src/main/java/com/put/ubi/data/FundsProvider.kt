package com.put.ubi.data

import com.put.ubi.model.Fund

class FundsProvider {
    fun getFunds(): List<Fund> {
        return listOf(
            Fund(
                id = "PCS74",
                name = "PKO Emerytura 2025",
                thumbnail = "https://www.mojeppk.pl/dam/jcr:4d6c353d-40a1-46d7-a2b9-c5332af85aa3/PKOTFI_logotyp.jpg"
            ),
            Fund(
                id = "PCS75",
                name = "PKO Emerytura 2030",
                thumbnail = "https://www.mojeppk.pl/dam/jcr:4d6c353d-40a1-46d7-a2b9-c5332af85aa3/PKOTFI_logotyp.jpg"
            ),
            Fund(
                id = "PCS76",
                name = "PKO Emerytura 2035",
                thumbnail = "https://www.mojeppk.pl/dam/jcr:4d6c353d-40a1-46d7-a2b9-c5332af85aa3/PKOTFI_logotyp.jpg"
            ),
            Fund(
                id = "PCS77",
                name = "PKO Emerytura 2040",
                thumbnail = "https://www.mojeppk.pl/dam/jcr:4d6c353d-40a1-46d7-a2b9-c5332af85aa3/PKOTFI_logotyp.jpg"
            ),
            Fund(
                id = "ALL82",
                name = "Allianz Plan Emerytalny 2025",
                thumbnail = "https://www.mojeppk.pl/dam/jcr:891d8e47-423b-425e-8592-6444b7ae8b1e/Allianz_TFI_logotyp_v3.png"
            ),
            Fund(
                id = "ALL83",
                name = "Allianz Plan Emerytalny 2030",
                thumbnail = "https://www.mojeppk.pl/dam/jcr:891d8e47-423b-425e-8592-6444b7ae8b1e/Allianz_TFI_logotyp_v3.png"
            ),
            Fund(
                id = "ALL84",
                name = "Allianz Plan Emerytalny 2035",
                thumbnail = "https://www.mojeppk.pl/dam/jcr:891d8e47-423b-425e-8592-6444b7ae8b1e/Allianz_TFI_logotyp_v3.png"
            ),
            Fund(
                id = "ALL85",
                name = "Allianz Plan Emerytalny 2040",
                thumbnail = "https://www.mojeppk.pl/dam/jcr:891d8e47-423b-425e-8592-6444b7ae8b1e/Allianz_TFI_logotyp_v3.png"
            ),

        )
    }
}