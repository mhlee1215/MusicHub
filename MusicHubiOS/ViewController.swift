//
//  ViewController.swift
//  MusicHubiOS
//
//  Created by Minhaeng Lee on 6/20/15.
//  Copyright (c) 2015 Minhaeng Lee. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func showMessage(){
        let alertController = UIAlertController(title:"test", message:"hello world", preferredStyle:UIAlertControllerStyle.Alert)
        alertController.addAction(UIAlertAction(title: "ok", style: UIAlertActionStyle.Default, handler: nil))
        self.presentViewController(alertController, animated: true, completion: nil)
    }

}

