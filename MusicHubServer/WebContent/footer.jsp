<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
</div><!-- /content -->

			<div class="jqm-footer">
				<p class="jqm-version"></p>
				<p>&copy; 2015, MusicHub Developing Team</p>
			</div><!-- /jqm-footer -->
			
			
			<style>
				.nav-search .ui-btn-up-a {
					background-image:none;
					background-color:#333333;
				}
				.nav-search .ui-btn-inner {
					border-top: 1px solid #888;
					border-color: rgba(255, 255, 255, .1);
				}
            </style>

				<div data-role="panel" data-position="left" data-position-fixed="false" data-display="reveal" id="nav-panel" data-theme="a">

					<ul data-role="listview" data-theme="a" data-divider-theme="a" style="margin-top:-16px;" class="nav-search">
						<li data-filtertext="wai-aria voiceover accessibility screen reader">
							<a href="index.do">Home</a>
						</li>
						
						<li data-filtertext="wai-aria voiceover accessibility screen reader">
							<a href="serverPage.do">I am a Server</a>
						</li>
						<li data-filtertext="accordions collapsible set collapsible-set collapsed">
							<a href="clientPage.do">I am a Client</a>
						</li>
						<li data-filtertext="ajax navigation model">
							<a href="../pages/page-navmodel.html">Setting</a>
						</li>
						<li data-filtertext="anatomy of page viewport">
							<a href="../pages/page-anatomy.html">Contact us</a>
						</li>
					</ul>

					<!-- panel content goes here -->
				</div><!-- /panel -->

				<style>
					.userform { padding:.8em 1.2em; }
					.userform h2 { color:#555; margin:0.3em 0 .8em 0; padding-bottom:.5em; border-bottom:1px solid rgba(0,0,0,.1); }
					.userform label { display:block; margin-top:1.2em; }
					.switch .ui-slider-switch { width: 6.5em !important }
					.ui-grid-a { margin-top:1em; padding-top:.8em; margin-top:1.4em; border-top:1px solid rgba(0,0,0,.1); }
                </style>

				<div data-role="panel" data-position="right" data-position-fixed="false" data-display="overlay" id="add-form" data-theme="b">

					<form class="userform">
						<h2>Create new user</h2>
						
						Facebook Login <img src="images/facebook-app-icon.png"/>
						
						
						<label for="name">Name</label>
						<input type="text" name="name" id="name" value="" data-clear-btn="true" data-mini="true">

						<label for="email">Email</label>
						<input type="email" name="email" id="status" value="" data-clear-btn="true" data-mini="true">

						<label for="password">Password:</label>
						<input type="password" name="password" id="password" value="" data-clear-btn="true" autocomplete="off" data-mini="true">

						<div class="switch">
						<label for="status">Status</label>
						<select name="status" id="slider" data-role="slider" data-mini="true">
						    <option value="off">Inactive</option>
						    <option value="on">Active</option>
						</select>
						</div>

						<div class="ui-grid-a">
						    <div class="ui-block-a"><a href="#" data-rel="close" data-role="button" data-theme="c" data-mini="true">Cancel</a></div>
						    <div class="ui-block-b"><a href="#" data-rel="close" data-role="button" data-theme="b" data-mini="true">Save</a></div>
						</div>
					</form>

					<!-- panel content goes here -->
				</div><!-- /panel -->
				
		</div><!-- /page -->
</html>