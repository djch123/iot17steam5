//
//  RecomViewController.swift
//  EmotionPlus
//
//  Created by Xiaozhe Wang on 4/28/17.
//  Copyright © 2017 iot17steam5. All rights reserved.
//

import UIKit
import SwiftyJSON
import Alamofire

class RecomViewController: UIViewController {
    
    var EMOTION_SERVER = "http://172.29.92.105:8888"
    var RECOMMENDATION_SERVER = "http://172.29.93.218:3000"
    
    var EMOTION_URL = ""
    var RESTAURANT_LIST_URL = ""
    var SPORT_LIST_URL = ""
    
    override func loadView() {
        super.loadView()
        
        self.EMOTION_URL = "\(EMOTION_SERVER)/emotion"
        self.RESTAURANT_LIST_URL = "\(RECOMMENDATION_SERVER)/recommendation/restaurant"
        self.SPORT_LIST_URL = "\(RECOMMENDATION_SERVER)/recommendation/sports"
        
        self.Core()
        
        weak var timer: Timer?
        timer = Timer.scheduledTimer(withTimeInterval: 10.0, repeats: true) { [weak self] _ in
            // do something here
            self?.Core()
        }
        
        weak var timer2: Timer?
        timer2 = Timer.scheduledTimer(withTimeInterval: 7.0, repeats: true) { [weak self] _ in
            // do something here
            if (self?.restaurantList.count)! > 0 {
                self?.restaurantIdx += 1
                if (self?.restaurantIdx)! >= (self?.restaurantList.count)! {
                    self?.restaurantIdx = 0
                }
                
                self?.changeRestaurantPage(idx: (self?.restaurantIdx)!)
                self?.restaurantsName.text = self?.restaurantNameArr[(self?.restaurantIdx)!]
            }
        }
        
        weak var timer3: Timer?
        timer3 = Timer.scheduledTimer(withTimeInterval: 10.0, repeats: true) { [weak self] _ in
            // do something here
            if (self?.sportList.count)! > 0 {
                self?.sportIdx += 1
                if (self?.sportIdx)! >= (self?.sportList.count)! {
                    self?.sportIdx = 0
                }
                
                self?.changeSportPage(idx: (self?.sportIdx)!)
                self?.sportName.text = self?.sportNameArr[(self?.sportIdx)!]
            }
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    var restaurantList: JSON = []
    var sportList: JSON = []
    
    var oldEmotion: String = ""
    var EMOTIONS: [String] = ["sadness","neutral","contempt","disgust","anger","surprise","fear","happiness"]
    func Core () {
        print("Core")
        // GET emotion
        // after obtain the response from GET
        // POST emotion and get response of a list of music
        // POST emotion and get response of HUE light brightness
        
        print(EMOTION_URL)
        Alamofire.request(
            URL(string: EMOTION_URL)!,
            method: .get).responseJSON { response in
                print(response)
                if var json = response.result.value as? [String: Any] {
                    // emotion get!
//                    print(json)
                    var maxIdx = self.EMOTIONS[0]
                    for emotion in self.EMOTIONS {
                        let val = json[emotion]! as! Float
//                        print(val)
                        if val > (json[maxIdx]! as! Float) {
                            maxIdx = emotion
                        }
                    }
                    
                    if maxIdx != self.oldEmotion {
                        self.oldEmotion = maxIdx
                        
                        self.restaurantNameArr = []
                        self.restaurantLinkArr = []
                        self.sportNameArr = []
                        
                        Alamofire.request(
                            URL(string: self.RESTAURANT_LIST_URL)!,
                            method: .post,
                            parameters: json,
                            encoding: JSONEncoding.default).responseJSON { response in
                                // RESTAURANT list get!
//                                print(response)
                                if let json = response.result.value{
                                    // RESTAURANT list json get!
                                    print("RESTAURANT json")
//                                    print(json)
                                    self.restaurantList = JSON(json)
                                    self.SetRestaurants()
                                }
                        }
                        Alamofire.request(
                            URL(string: self.SPORT_LIST_URL)!,
                            method: .post,
                            parameters: json,
                            encoding: JSONEncoding.default).responseJSON { response in
                                // SPORT list get!
//                                print(response)
                                if let json = response.result.value{
                                    // SPORT list json get!
                                    print("SPORT json")
//                                    print(json)
                                    self.sportList = JSON(json)
                                    self.SetSports()
                                }
                        }
                    }
                    
                }
        }
    }

    @IBOutlet weak var restaurantsScrollView: UIScrollView!
    @IBOutlet weak var restaurantsName: UILabel!
    @IBOutlet weak var sportScrollView: UIScrollView!
    @IBOutlet weak var sportName: UILabel!
    //    var restaurantImgArr : [UIImage] = []
    var restaurantNameArr : [String] = []
    var restaurantLinkArr : [String] = []
    
    var sportNameArr : [String] = []
    
    func SetRestaurants() {
        for (i, restaurant) in self.restaurantList {
            //            print ("new image!!!!!")
            let img = restaurant["img"]
            let name = restaurant["name"]
            let link = restaurant["url"]
            if let url = URL(string: "http://\(img)".addingPercentEncoding(withAllowedCharacters: .urlPathAllowed)!) {
                if let data = try? Data(contentsOf: url) {
//                    print ("add image!!!!!")
                    let newimage = UIImage(data: data)
//                    self.restaurantImgArr.append(newimage!)
                    self.restaurantNameArr.append("\(name)")
                    self.restaurantLinkArr.append("\(link)")
                    
                    let newImageView:UIImageView = UIImageView()
                    newImageView.image = newimage
                    
                    var frame = CGRect.zero
                    frame.origin.x = self.restaurantsScrollView.frame.size.width * CGFloat(Float(i)!)
                    frame.origin.y = 0
                    frame.size = self.restaurantsScrollView.frame.size
                    newImageView.frame = frame
                    newImageView.contentMode = UIViewContentMode.scaleAspectFit
                    newImageView.clipsToBounds = true
                    
                    let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(imageTapped(tapGestureRecognizer:)))
                    newImageView.isUserInteractionEnabled = true
                    newImageView.addGestureRecognizer(tapGestureRecognizer)
                    
                    self.restaurantsScrollView.addSubview(newImageView)
                }
            }
        }
        
        self.restaurantsName.text = self.restaurantNameArr[0]
    }
    
    var restaurantIdx = Int(0)
    var sportIdx = Int(0)
    
    func changeRestaurantPage(idx : Int) {
        let x = CGFloat(idx) * self.restaurantsScrollView.frame.size.width
        self.restaurantsScrollView.setContentOffset(CGPoint(x: x,y :0), animated: true)
    }
    
    func changeSportPage(idx : Int) {
        let x = CGFloat(idx) * self.sportScrollView.frame.size.width
        self.sportScrollView.setContentOffset(CGPoint(x: x,y :0), animated: true)
    }
    
    func imageTapped(tapGestureRecognizer: UITapGestureRecognizer)
    {
        // Your action
        openURL(link: self.restaurantLinkArr[self.restaurantIdx])
    }
    
    func openURL(link: String){
        if let url = URL(string: link) {
            UIApplication.shared.openURL(url)
        }
    }
    
    func SetSports() {
        for (i, sport) in self.sportList {
            //            print ("new image!!!!!")
            let img = sport["img"]
            let name = sport["name"]
            if let url = URL(string: "http://\(img)".addingPercentEncoding(withAllowedCharacters: .urlPathAllowed)!) {
                if let data = try? Data(contentsOf: url) {
                    //                    print ("add image!!!!!")
                    let newimage = UIImage(data: data)
                    self.sportNameArr.append("\(name)")
                    
                    let newImageView:UIImageView = UIImageView()
                    newImageView.image = newimage
                    
                    var frame = CGRect.zero
                    frame.origin.x = self.sportScrollView.frame.size.width * CGFloat(Float(i)!)
                    frame.origin.y = 0
                    frame.size = self.sportScrollView.frame.size
                    newImageView.frame = frame
                    newImageView.contentMode = UIViewContentMode.scaleAspectFit
                    newImageView.clipsToBounds = true
                    
                    self.sportScrollView.addSubview(newImageView)
                }
            }
        }
        
        self.sportName.text = self.sportNameArr[0]
//        print ("sports count")
//        print (self.sportList.count)
//        print (self.sportNameArr.count)
    }
}
