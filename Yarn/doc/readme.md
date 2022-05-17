# Yarn基础

Yet Another Resource Negotiatior

cluster resource management 集群资源管理

hadoop 集群的资源管理系统  ResourceManager

负责集群的资源管理和调度

十多种计算框架可以运行在一个集群中。

特点：
    良好的扩展性，高可用。
    对多种类型应用进行统一管理和调度
    自带了多种用户调度器，适合共享集群环境
    相比传统模式，提高了资源利用率，降低运维成本和数据共享成本

---

# hadoop 1.0 和 2.0 对比
2.0 zookeeper 故障转移

MRv1 不适合场景：实时场景，迭代场景，图计算 （job tracker、task tracker）
    job tracker：资源管理（cpu，内存），作业调度以及监控
    task tracker：作业执行 3s向JobTracker 汇报状态
    job tracker 需要不断跟踪所有task tracker 和所有的map、reduce任务，task tracker上的任务都是有job tracker分配。
MRv2 
    相对于 MRv1 可优化方向：
        减少了单个JobTracker的职责，将部分职责指派给TaskTracker，因为集群中有许多TaskTracker。
        不再拥有JobTracker，引入集群管理器，负责跟踪集群中的活动节点和可用资源，并将他们分配给任务。
        对于提交个集群的每一个作业，都会启动一个专用的短暂的JobTracker来控制该作业中的任务执行，短暂的JobTracker有在从属节点上运行的TaskTracker启动

    
    yarn： 在做一次总结理解
    RM ResourceManager （集群管理器） 资源管理 有可插拔的调度组件（ Scheduler 纯粹的调度器 负责运行中的各种应用分配资源 不负责应用程序的监控和跟踪）
    AM ApplicationMaster （代替的一个专用的短暂的JobTracker）作业调度和监控 本质也是一个container（管理其他container）不用常驻
    NM NodeManager （TaskTracker）节点进程，启动进程，管理节点资源
    container ： 两类container 内存、cpu

    当MR执行结束，可以释放Map的COntainer，并请求个多的Reduce Container


RM ：
    RM处理客户端请求，接收了JobSubmitter提交的作业，按照作业的上下文context信息，以及NodeManager(NM)收集的状态信息，启动调度过程，分配一个container作为ApplicationMaster(AM)

    RM拥有为系统中左右应用资源分配的决定权，是中心服务。调度、启动每一个Job所属的Application、监控Application的存在执行情况。

    与运行在每一个节点上的NM进行交互，通过心跳通信，达到监控NM的目的。

    RM拥有一个可插拔的调度器组件Scheduler
        一个纯粹的调度器：不负责应用程序的监控和状态跟踪，不保证应用程序失败或者硬件失败的情况下对Task重启


NM：
    属于slave进程，类似于TaskTracker的角色，是每个机器框架代理
    
    处理来自RM的任务请求

    接受并处理AM的Container的启动、停止等各种请求

    负责启动应用程序的container （执行应用程序的容器），并监控他们的资源使用情况（cpu、内存、磁盘、网络等），并报告给RM

    单节点上的进行资源管理和任务调度

AM：
    用户作业的主进程，应用程序的Master，每个应用对应一个AM，在用户提交一个应用程序时，一个AM的轻量型进程实例启动，协调应用内所有任务的执行。

    负责一个Job的生命周期内所有的工作。

    与RM协商资源
        与Scheduler协商合适的Container

    与NM协同工作，与Scheduler协商合适的Container的监控

    身份可视为特殊的container。

    AM利用多台机器处理能力完成一个作业，为了实现该目标，AM向RM申请资源（Container）交给NM管理，集群以Container的形式运转AM的应用。 运行中的container可能是map或者reduce。Container需要和AM通信，汇报任务状态和健康信息。

Container：
    是任务运行环境（cpu（虚拟的cpu，由yarn引入的概念）、内存、磁盘、网络等）的抽象封装。
    ps：
        虚拟cpu的概念：协调不同cpu的性能。

    Container只是使用NM指定资源的权利。

    AM必须向NM提供更多的信息来启动Container。

Yarn框架的运行过程：
    待补充



Yarn 容错能力
    RM：
        单点故障，可基于Zookeeper实现HA高可用集群，可通过配置进行设置准备RM，主提供服务，备同步主的信息，一旦主挂掉，备立即做切换接替进行服务。
    NM：
        当其中一个挂掉，会通过心跳方式通知RM，RM将情况通知对应的AM，AM在做进一步的处理
    AM：
        如果AM挂掉，RM负责重启，其实RM上有一个RMApplicationMaster，是AM的AM，上面保存已经完成的Task，如果重启AM，也可以无需重新运行已经完成的Task

Yarn 调度器
    FIFO ：顺序调度。大应用占用所有集群资源，不适合共享集群。

    Capacity ：专有队列运转小任务。预先占用一定集群资源，导致大任务执行时间落后于FIFO
    
    Fair ：不需要预占用，动态调整，公平共享。


    
