import {getAllCust} from "../../api/custApi"

const customer = () => {
}

export async function fetchCustomers(id) {
    try {
        const res = await getAllCust(id);

        const formatted = res.data.map((item) => ({
            value: item.custId,
            label: item.custName,
            isGst : item.isGst,
        }));

        return formatted;

    } catch(e)
    {
        return e;
    }
}