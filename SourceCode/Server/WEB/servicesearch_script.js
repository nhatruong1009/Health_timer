const url='http://localhost:803';

//function ServiceSearch(searchstr=""){
function ServiceSearch(searchstr){
    let api = '/Service/search'

    const Http = new XMLHttpRequest();
    Http.open("GET", url+api+`?searchstr=${searchstr}`,true);
    Http.onload = function(){
        resp = JSON.parse(Http.responseText);
        // do sthg here
        console.log(resp)
    };
    Http.send();
}