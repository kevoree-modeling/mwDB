enum hello.SmartMeterState {
    InError, Healthy, Pending
}

index nodes : sample.Node {
    name
}

class sample.Cloud {
    rel nodes : sample.Node
}
class sample.Node {
    att name : String {

    }
    rel softwares : sample.Software
}
class sample.Software {
    att name : String {

    }
    att load : Double
    //att load : Double {
    //    using "Polynomial"
    //    with precision "0.1"
    //}
}
