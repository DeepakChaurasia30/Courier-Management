import { getTableData } from "../../api/invoice";


export async function getInvoiceTableData(clientID)
{

    try {
        
        const res = await getTableData(clientID);

        return res.data;
    } catch (error) {
        return error;
    }
}