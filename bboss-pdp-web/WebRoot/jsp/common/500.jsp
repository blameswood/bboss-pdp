<%@ page session="false" language="java"
	contentType="text/html; charset=utf-8"%>

<%@ taglib uri="/WEB-INF/tld/admin-taglib.tld" prefix="admin" %>
<!-- BEGIN PAGE LEVEL STYLES -->
        <link href="${pageContext.request.contextPath}/assets/pages/css/error.min.css" rel="stylesheet" type="text/css" />
        <!-- END PAGE LEVEL STYLES -->
<!-- BEGIN CONTENT BODY -->

                    <!-- BEGIN PAGE BAR -->
                    <div class="page-bar">
                        <admin:menuposition/>
                        <div class="page-toolbar">
                            <div class="btn-group pull-right">
                                <button type="button" class="btn green btn-sm btn-outline dropdown-toggle" data-toggle="dropdown"> Actions
                                    <i class="fa fa-angle-down"></i>
                                </button>
                                <ul class="dropdown-menu pull-right" role="menu">
                                    <li>
                                        <a href="#">
                                            <i class="icon-bell"></i> Action</a>
                                    </li>
                                    <li>
                                        <a href="#">
                                            <i class="icon-shield"></i> Another action</a>
                                    </li>
                                    <li>
                                        <a href="#">
                                            <i class="icon-user"></i> Something else here</a>
                                    </li>
                                    <li class="divider"> </li>
                                    <li>
                                        <a href="#">
                                            <i class="icon-bag"></i> Separated link</a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <!-- END PAGE BAR -->
                   
                    <!-- END PAGE HEADER-->
                    <div class="row">
                            <div class="col-md-12 page-500">
                                <div class=" number font-red"> 500 </div>
                                <div class=" details">
                                    <h3>Oops! Something went wrong.</h3>
                                    <p> We are fixing it! Please come back in a while.
                                        <br/> </p>
                                    <p>
                                        <a href="index.html" class="btn red btn-outline"> Return home </a>
                                        <br> </p>
                                </div>
                            </div>
                        </div>
                
                <!-- END CONTENT BODY -->