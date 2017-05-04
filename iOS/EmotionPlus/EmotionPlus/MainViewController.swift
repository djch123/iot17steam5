//
//  FirstViewController.swift
//  EmotionPlus
//
//  Created by Xiaozhe Wang on 4/28/17.
//  Copyright © 2017 iot17steam5. All rights reserved.
//

import UIKit
import Charts

class MainViewController: UIViewController, ChartViewDelegate, IAxisValueFormatter, UITableViewDelegate, UITableViewDataSource {
    
    var activities : [String] = []
    
    /// Called when a value from an axis is formatted before being drawn.
    ///
    /// For performance reasons, avoid excessive calculations and memory allocations inside this method.
    ///
    /// - returns: The customized label that is drawn on the x-axis.
    /// - parameter value:           the value that is currently being drawn
    /// - parameter axis:            the axis that the value belongs to
    ///
    public func stringForValue(_ value: Double, axis: AxisBase?) -> String {
        return activities[Int(value) % activities.count]
    }

    @IBOutlet var history: UITableView!

    let dformatter = DateFormatter()
    
    @IBOutlet var radar: RadarChartView!
    override func viewDidLoad() {
        super.viewDidLoad()
        print("aaaaa")
        self.dformatter.dateFormat = "MMMM dd, yyyy"
        
        
        history.delegate = self
        history.dataSource = self
        
        
        self.title = "Radar Bar Chart"
        self.activities = ["Neutral", "Happiness", "Disgust", "Anger", "Surprise", "Fear", "Sadness", "Contempt"]
        radar.delegate = self;
        radar.chartDescription?.enabled = false;
        radar.webLineWidth = 1.0
        radar.innerWebLineWidth = 1.0
        radar.webColor = UIColor.lightGray
        radar.innerWebColor = UIColor.lightGray
        radar.webAlpha = 1.0
        
        let marker : RadarMarkerView = RadarMarkerView.viewFromXib() as! RadarMarkerView
        marker.chartView = radar
        radar.marker = marker
        
        radar.xAxis.labelFont = UIFont(name: "HelveticaNeue-Light", size: CGFloat(9))!
        radar.xAxis.xOffset = 0.0
        radar.xAxis.yOffset = 0.0
        radar.xAxis.valueFormatter = self;
        radar.xAxis.labelTextColor = UIColor.white
        
        
        radar.yAxis.labelFont = UIFont(name: "HelveticaNeue-Light", size: CGFloat(9))!
        radar.yAxis.labelCount = 5
        radar.yAxis.axisMinimum = 0.0
        radar.yAxis.axisMaximum = 100
        radar.yAxis.drawLabelsEnabled = false
        
        radar.legend.horizontalAlignment = Legend.HorizontalAlignment.center
        radar.legend.verticalAlignment = Legend.VerticalAlignment.top
        radar.legend.orientation = Legend.Orientation.horizontal
        radar.legend.drawInside = false
        radar.legend.font = UIFont(name: "HelveticaNeue-Light", size: 10.0)!
        radar.legend.xEntrySpace = 7.0
        radar.legend.yEntrySpace = 5.0
        radar.legend.textColor = UIColor.white
        radarLoadData(num: 0, date: "TODAY")
        getWeekHistory()
        print("bbbbb")
        
        
    }
    
    
    func radarLoadData(num : Int, date : String) {
        
        let weekData = weekHistory[num]
        var sum = 0
        var max = 0;
        for n in weekData{
            sum += n
            if n > max{
                max = n
            }
        }
        var loadData : [RadarChartDataEntry] = []
        if max == 0 {
            for _ in weekData{
                loadData.append(RadarChartDataEntry(value: Double(1)/Double(8)*100))
            }
            radar.yAxis.axisMaximum = Double(1)/Double(8) * 100.0
        } else {
            for n in weekData{
                loadData.append(RadarChartDataEntry(value: Double(n)/Double(sum)*100))
            }
            radar.yAxis.axisMaximum = Double(max)/Double(sum) * 100.0
        }
        
        let set1 : RadarChartDataSet = RadarChartDataSet(values: loadData, label: date)
        set1.setColor(UIColor(red: 103/255.0, green: 110/255.0, blue: 110/255.0, alpha: 1.0))
        set1.fillColor = UIColor(red: 103/255.0, green: 110/255.0, blue: 110/255.0, alpha: 1.0)
        set1.drawFilledEnabled = true
        set1.fillAlpha = 0.7
        set1.lineWidth = 2.0
        set1.drawHighlightCircleEnabled = true
        set1.setDrawHighlightIndicators(false)
        
        let data : RadarChartData = RadarChartData(dataSets: [set1])
        data.setValueFont(UIFont(name: "HelveticaNeue-Light", size: 8.0))
        data.setDrawValues(false)
        data.setValueTextColor(UIColor.white)
        radar.data = data
        
        radar.animate(xAxisDuration: 1.4, yAxisDuration: 1.4, easingOption: ChartEasingOption.easeOutBack)

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Table view data source
    
    func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return 7
    }
    
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier = "HistoryTableViewCell"
        guard let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as? HistoryTableViewCell else {
            fatalError("The dequeued cell is not an instance of HistoryTableViewCell.")
        }
        if(weekHistory.count > indexPath.row){
            if(indexPath.row != 0) {
                let now = Date()
                let perDay = 24*60*60
                let thatDay = now.addingTimeInterval(TimeInterval(-1 * indexPath.row * perDay))
                cell.label.text = dformatter.string(from: thatDay)
            } else{
                cell.label.text = "TODAY"
            }
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath){
        var text = ""
        if(indexPath.row != 0) {
            let now = Date()
            let perDay = 24*60*60
            let thatDay = now.addingTimeInterval(TimeInterval(-1 * indexPath.row * perDay))
            text = dformatter.string(from: thatDay)
        } else{
            text = "TODAY"
        }
        radarLoadData(num: indexPath.row, date: text)
    }
    
    var weekHistory : [[Int]] = [[14,17,12,15,8,5,6,10],[10,1,5,9,12,13,13],[2,9,14,1,11,6,12,8],[6,14,2,3,18,16,4,9],
                                 [2,0,19,10,11,16,17,17],[7,19,14,1,18,12,17,0],[14,17,12,15,8,5,6,10]]
    
    func getWeekHistory(){
        //create the url with NSURL
        let url = URL(string: "http://172.29.92.105:8888/emotion/week")! //change the url
        
        let task = URLSession.shared.dataTask(with: url) { data, response, error in
            guard error == nil else {
                print(error!)
                return
            }
            guard let data = data else {
                print("Data is empty")
                return
            }
            let json = try! JSONSerialization.jsonObject(with: data, options: []) as! [String : Any]
            let week = json["data"] as! [[String: Any]]
            self.weekHistory = []
            for day in week{
                var dayHistory : [Int] = []
                dayHistory.append(day["neutral"] as! Int)
                dayHistory.append(day["happiness"] as! Int)
                dayHistory.append(day["disgust"] as! Int)
                dayHistory.append(day["anger"] as! Int)
                dayHistory.append(day["surprise"] as! Int)
                dayHistory.append(day["fear"] as! Int)
                dayHistory.append(day["sadness"] as! Int)
                dayHistory.append(day["contempt"] as! Int)
                self.weekHistory.insert(dayHistory, at: 0)
            }
        }
        task.resume()
    }
}

