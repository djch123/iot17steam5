//
//  ViewController.swift
//  EmotionPlus
//
//  Created by Xiaozhe Wang on 5/2/17.
//  Copyright © 2017 iot17steam5. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        let scrollView = UIScrollView()
        scrollView.frame = self.view.bounds
        let imageView = UIImageView(image:UIImage(named:"sky-clouds-blue-horizon"))
        scrollView.contentSize = CGSize(width: imageView.bounds.size.width*2, height: imageView.bounds.size.height*2)
//        scrollView.addSubview(imageView)
        self.view.addSubview(scrollView)
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
