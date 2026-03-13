   export function getFinacialYear(startyear=2020){
   const date = new Date();
    const years = [];

    let curentYear = date.getYear() + 1900;

    if (date.getMonth() > 3) {
      curentYear = date.getYear() + 1901;
    }


    for (let i = startyear; i < curentYear; i++) {

      years.push(`${i.toString().slice(2)}-${(i + 1).toString().slice(2)}`)

    }
    // console.log(years);
    return years.reverse();
  }


