import { getSummary } from "../../api/awbApi";


export async function getSummaryAWB(custID)
{
    try {

        const res = await getSummary(custID);

        return res.data;
        
    } catch (error) {
        
        return error;
    }
}