//
//  SecondViewController.swift
//  EmotionPlus
//
//  Created by Xiaozhe Wang on 4/28/17.
//  Copyright © 2017 iot17steam5. All rights reserved.
//

import UIKit
import Foundation

class GameViewController: UIViewController, UINavigationControllerDelegate, UIImagePickerControllerDelegate, ChartViewDelegate, IAxisValueFormatter {

    var activities : [String] = ["Anger", "Fear", "Surprise", "Contempt", "Disgust", "Happiness", "Neutral", "Sadness"]
    
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
    
    var imageView: UIImageView!
    
    @IBOutlet var button: UIButton!
    
    var imagePicker: UIImagePickerController!
    var imageYou : UIImageView!
    var radar: RadarChartView!
    
    var state = 0
    
    @IBAction func takePhoto(_ sender: Any) {
        if self.state == 0 {
            imagePicker =  UIImagePickerController()
            imagePicker.delegate = self
            imagePicker.sourceType = .camera
            present(imagePicker, animated: true, completion: nil)
        } else {
            calOrigin()
        }
        
    }
    
    var num = 0
    var scrollView : UIScrollView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.scrollView = UIScrollView()
        self.scrollView.frame = CGRect(x: 0, y: 0, width: 375, height: 572)
        self.imageView = UIImageView(image:UIImage(named:"face0"))
        self.imageView.frame = CGRect(x: 0, y: 0, width: 375, height: 572)
        self.imageView.bounds = CGRect(x: 0, y: 0, width: 375, height: 572)
        self.imageView.contentMode = UIViewContentMode.scaleAspectFill
        scrollView.contentSize = CGSize(width: imageView.bounds.size.width, height: imageView.bounds.size.height*3)
        scrollView.addSubview(imageView)
        self.imageYou = UIImageView()
        self.imageYou.frame = CGRect(x: 0, y: 572, width: 375, height: 572)
        self.imageYou.bounds = CGRect(x: 0, y: 0, width: 375, height: 572)
        self.imageYou.contentMode = UIViewContentMode.scaleAspectFill
        scrollView.addSubview(imageYou)
        
        
        scrollView.backgroundColor = UIColor(red: 46, green: 49, blue: 64, alpha: 1)
        self.radar = RadarChartView(frame: CGRect(x: 0, y: 572*2, width: 375, height: 572))
        scrollView.addSubview(radar)
        self.radar.backgroundColor = UIColor(red: 46, green: 49, blue: 64, alpha: 1)
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
        radar.xAxis.labelTextColor = UIColor.black
        
        
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
        radar.legend.textColor = UIColor.black
        
        self.view.addSubview(scrollView)
        
        scrollView.backgroundColor = UIColor(red: 46, green: 49, blue: 64, alpha: 1)
        calOrigin()
        
    }
    
    func radarLoadData() {
        
        var max : Float = 0.0;
        for n in originFace{
            if n > max{
                max = n
            }
        }
        for n in yourFace{
            if n > max{
                max = n
            }
        }
        radar.yAxis.axisMaximum = Double(max)
        
        var loadData1 : [RadarChartDataEntry] = []
        for n in originFace{
            loadData1.append(RadarChartDataEntry(value: Double(n)))
        }
        let set1 : RadarChartDataSet = RadarChartDataSet(values: loadData1, label: "origin")
        set1.setColor(UIColor(red: 103/255.0, green: 110/255.0, blue: 110/255.0, alpha: 1.0))
        set1.fillColor = UIColor(red: 103/255.0, green: 110/255.0, blue: 110/255.0, alpha: 1.0)
        set1.drawFilledEnabled = true
        set1.fillAlpha = 0.7
        set1.lineWidth = 2.0
        set1.drawHighlightCircleEnabled = true
        set1.setDrawHighlightIndicators(false)
        
        if yourFace.count != 0 {
            var loadData2 : [RadarChartDataEntry] = []
            for n in yourFace{
                loadData2.append(RadarChartDataEntry(value: Double(n)))
            }
            let set2 : RadarChartDataSet = RadarChartDataSet(values: loadData2, label: "yours")
            set2.setColor(UIColor(red: 121/255.0, green: 162/255.0, blue: 175/255.0, alpha: 1.0))
            set2.fillColor = UIColor(red: 121/255.0, green: 162/255.0, blue: 175/255.0, alpha: 1.0)
            set2.drawFilledEnabled = true
            set2.fillAlpha = 0.7
            set2.lineWidth = 2.0
            set2.drawHighlightCircleEnabled = true
            set2.setDrawHighlightIndicators(false)
            
            
            let data : RadarChartData = RadarChartData(dataSets: [set1,set2])
            data.setValueFont(UIFont(name: "HelveticaNeue-Light", size: 8.0))
            data.setDrawValues(false)
            data.setValueTextColor(UIColor.black)
            radar.data = data
        } else {
            let data : RadarChartData = RadarChartData(dataSets: [set1])
            data.setValueFont(UIFont(name: "HelveticaNeue-Light", size: 8.0))
            data.setDrawValues(false)
            data.setValueTextColor(UIColor.black)
            radar.data = data
        }
        
        
        radar.animate(xAxisDuration: 1.4, yAxisDuration: 1.4, easingOption: ChartEasingOption.easeOutBack)
        
    }
    
    func calOrigin() {
        self.button.setTitle("Play", for: UIControlState.normal)
        self.score.text = ""
        self.state = 0
        
        self.imageView.image = UIImage(named: "face\(self.num)")
        self.imageYou.image = UIImage()
        self.scrollView.setContentOffset(CGPoint(x: 0, y: 0), animated: true)
        self.num  = (self.num + 1) % 8
        let headers = [
            "content-type": "application/octet-stream"
        ]
        
        let postData = NSData.init(data: UIImageJPEGRepresentation(imageView.image!, 0.7)!)
        
        //        NSData(data: "dddd".data(using: String.Encoding.utf8)!)
        
        let request = NSMutableURLRequest(url: NSURL(string: "http://172.29.93.218:3000/analyze")! as URL,
                                          cachePolicy: .useProtocolCachePolicy,
                                          timeoutInterval: 10.0)
        request.httpMethod = "POST"
        request.allHTTPHeaderFields = headers
        request.httpBody = postData as Data
        
        let session = URLSession.shared
        let dataTask = session.dataTask(with: request as URLRequest, completionHandler: { (data, response, error) -> Void in
            if (error != nil) {
                print(error)
            } else {
                let httpResponse = response as? HTTPURLResponse
                print(httpResponse)
                if httpResponse?.statusCode == 200{
                    let json = try! JSONSerialization.jsonObject(with: data!, options: []) as! [String : Any]
                    print(json)
                    self.originFace = []
                    self.originFace.append(json["anger"] as! Float)
                    self.originFace.append(json["fear"] as! Float)
                    self.originFace.append(json["surprise"] as! Float)
                    self.originFace.append(json["contempt"] as! Float)
                    self.originFace.append(json["disgust"] as! Float)
                    self.originFace.append(json["happiness"] as! Float)
                    self.originFace.append(json["neutral"] as! Float)
                    self.originFace.append(json["sadness"] as! Float)
                }
            }
        })
        
        dataTask.resume()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        imagePicker.dismiss(animated: true, completion: nil)
        imageYou.image = info[UIImagePickerControllerOriginalImage] as? UIImage
        
        let headers = [
            "content-type": "application/octet-stream"
        ]
        
        let postData = NSData.init(data: UIImageJPEGRepresentation(imageYou.image!, 0.7)!)
        
//        NSData(data: "dddd".data(using: String.Encoding.utf8)!)
        
        let request = NSMutableURLRequest(url: NSURL(string: "http://172.29.93.218:3000/analyze")! as URL,
                                          cachePolicy: .useProtocolCachePolicy,
                                          timeoutInterval: 10.0)
        request.httpMethod = "POST"
        request.allHTTPHeaderFields = headers
        request.httpBody = postData as Data
        
        let session = URLSession.shared
        let dataTask = session.dataTask(with: request as URLRequest, completionHandler: { (data, response, error) -> Void in
            if (error != nil) {
                print(error)
            } else {
                let httpResponse = response as? HTTPURLResponse
                print(httpResponse)
                if httpResponse?.statusCode == 200{
                    let json = try! JSONSerialization.jsonObject(with: data!, options: []) as! [String : Any]
                    print(json)
                    self.yourFace = []
                    self.yourFace.append(json["anger"] as! Float)
                    self.yourFace.append(json["fear"] as! Float)
                    self.yourFace.append(json["surprise"] as! Float)
                    self.yourFace.append(json["contempt"] as! Float)
                    self.yourFace.append(json["disgust"] as! Float)
                    self.yourFace.append(json["happiness"] as! Float)
                    self.yourFace.append(json["neutral"] as! Float)
                    self.yourFace.append(json["sadness"] as! Float)
                    var sum : Float = 0.0
                    for i in 0..<self.originFace.count {
                        sum += abs(self.originFace[i] - self.yourFace[i])
                    }
                    let s : Float = round((2.0 - sum) / 2.0 * 100)
                    DispatchQueue.main.sync() {
                        self.score.text = String(s)
                        self.button.setTitle("Refresh", for: UIControlState.normal)
                        self.state = 1
                        self.radarLoadData()
                    }
                    print(String(s))
                } else {
                    self.yourFace = []
                    DispatchQueue.main.sync() {
                        self.score.text = "0"
                        self.button.setTitle("Refresh", for: UIControlState.normal)
                        self.state = 1
                        self.radarLoadData()
                    }
                }
            }
        })
        
        dataTask.resume()
    }
    
    var originFace : [Float] = []
    var yourFace : [Float] = []
    
    @IBOutlet var score: UILabel!
    
}

