----------
### Decaf Java

Prototype implementation of a drag-and-drop source code editor for Java.
----------

#### == Status ==

This is an archive of an entrepreneurial venture that ended in 2004. Our goal was to license DecafJava to software vendors who needed a generic plugin editor suitable for less technically experienced users. The dependency on a JVM and the clumsy JNI interface became significant obstacles, despite our efforts to provide a usable JNI layer. Successful products typically needed only a small interpreter compiled for the native platform that could be easily installed with the vendor's product, or even integrated directly.

**Running DecafJava** may be difficult on current platforms because it was built on Windows XP in 2003, and relies on several awkward layers to interface with native executables written in C/C++. However, all the source code is available in the repository, and all of the third party libraries are still supported by the original developer or organization, so it should be possible to construct a working version on your favorite PC or VM. 

#### == Usage Model ==

User interfaces often give the user a way to customize various features of the application, for example an email client typically enables the user to create filters that automatically perform actions on a select subset of incoming email messages. Developers can use DecafJava to automatically generate the windows and panels that provide access to this kind of customization. It only requires a simple XML file to describe which applications interfaces to expose, and DecafJava generates the corresponding windows along with JNI bindings. The customization windows are populated with a widget for each token of Java syntax in the interface method declarations, and the panels support text entry and drag-and-drop operations corresponding to the syntax rules of the Java language. This approach effectively enables the user to create, compile and install custom Java plugins for their application (such as email filters), all without writing a single line of code or performing any configuration. 

#### == Vision ==

Customizability is highly valued in certain kinds of applications, for example an email client that is used regularly, or a video editor where the user's entire project investment--time, talent, creative reflection--is summed up and carried by the editor's output files. But the cost and risk of developing flexible software has become such a strong deterrent that application design has been trending more and more towards the extremes of simplicity for over a decade. DecafJava was envisioned and built before this trend began, and the author is not surprised to see it unfold because it was well evident even in 2000 that the effort necessary to build a successfully customizable interface is rarely worth the benefit. 

**Common practice** in software development exaggerates the challenge in some ways: by designing and implementing a separate user interaction handle for each customizable software element, the potential for feature inerference and plain-old-error is maximized, along with the documentation and testing workload. 

**The hidden potential** that DecafJava was designed to unlock is simply that for every customizable value that can be exposed to the user, the program must already have some kind of internal interface to that value. If there is additional work necessary to expose that interface to the user, it is either because the component is not easily accessible from the interactive code, or because it takes a form that is fundamentally non-intuitive for a human user. Imagine each of these internal interfaces as a feature on the surface of a plane. In the context of this metaphor, what is human intuition about the program components? It is simply a reconfiguration of the same content into a different set of contours. Now image the plane of *user intuition contours* is floating at some distance parallel to the program's plane of *internal interfaces contours*. Instead of generating a complex web of handles between the two layers, can we find a generic basis of transformation that would naturally serve as a universal adapter between the two layers? 

**The key idea** of DecafJava is to provide a framework for developing this kind of natural adapter between internal data structures and user interaction components. Ideally the developer would not create any new user interface components, but simply configure some DecafJava panels with representative text and images, and then bind the DecafJava panels directly to the internal data interfaces. Since the components on a DecafJava panel are inherently bound to AST tokens in the Java compiler's front end (hence the name *DecafJava*), user interaction maps automatically to code changes in a dynamically-generated adapter layer. Java syntax rules are pre-configured in all DecafJava panels, such that every interaction available to the user will map to a syntactically valid code change. This approach reduces the developer effort of making internal data interfaces accessibie--it is only necessary to create an XML configuration file for each DecafJava panel and initialize it as necessary.

**User intuition** is a mysterious phenomenon that may not always be directly compatible with internal program structure. For example, people talk about "that email" as if it were a single cohesive thing, whereas its concrete representation in software is divided into several distinct data elements (a subject, a sender, some receivers, the text, etc.), and many copies of that group of elements are held by the email server, its database or filestore, and the user's email program. Where the "human view" does not map one-to-one onto the "digital view", the developer can create a *human computer interaction* (HCI) *adapter* to perform any transformational steps necessary. A suitable metaphor in the context of the earlier parallel-plane example, an HCI adapter acts as a [tensor](https://en.wikipedia.org/wiki/Tensor) to convert between the program's perspective and the user's perspective. This approach is based on the [Tensor Modeling](http://people.rennes.inria.fr/Byron.Hawkins/cv/tensor-modeling.html) technique.

**The scientific advantage** of DecafJava goes beyond refactoring and a dynamically generated user interface. Given only those two features, the developer may still create a tangled web of HCI adapters, which may be no less cumbersome than the common practice of implementing individual user interface handles. We do not prevent chaos in the developer's code. But DecafJava enables the astute developer to coalesce all the HCI adapters into a cohesive layer that can gracefully coordinate any potential interference among the adapters, or between adapters and other consumers of the application's internal data interfaces. Intuitively, this architecture provides the multi-dimensional isolation of *aspect-oriented programming* while avoiding the clumsy advice mechanism and the inflexibility of aspect weaving. The HCI adapter layer can integrate as much complex logic as necessary to make a clean and well-organized transformation between the user's view of the application data and the appliction's internal construction. 
